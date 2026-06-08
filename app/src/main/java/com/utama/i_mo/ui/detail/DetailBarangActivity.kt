package com.utama.i_mo.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.utama.i_mo.R
import com.utama.i_mo.databinding.ActivityDetailBarangBinding
import com.utama.i_mo.model.Barang
import com.utama.i_mo.repository.BarangRepository
import com.utama.i_mo.utils.Constants
import com.utama.i_mo.viewmodel.BarangViewModel
import com.utama.i_mo.viewmodel.BarangViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class DetailBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBarangBinding
    private lateinit var viewModel: BarangViewModel
    private var barangId: Int = 0
    private var currentBarang: Barang? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barangId = intent.getIntExtra(Constants.KEY_BARANG_ID, 0)
        if (barangId == 0) { finish(); return }

        setupToolbar()
        setupViewModel()
        setupKategoriDropdown()
        setupClickListeners()
        observeViewModel()
    }

    // ══════════════════════════════════════════════════════
    // SETUP
    // ══════════════════════════════════════════════════════

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.title_detail_barang)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViewModel() {
        val factory = BarangViewModelFactory(BarangRepository())
        viewModel = ViewModelProvider(this, factory)[BarangViewModel::class.java]
        viewModel.loadBarangById(barangId)
    }

    private fun setupKategoriDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            Constants.KATEGORI_LIST
        )
        binding.actvKategori.setAdapter(adapter)
    }

    private fun setupClickListeners() {
        binding.btnEdit.setOnClickListener {
            setEditMode(true)
        }

        binding.btnSimpan.setOnClickListener {
            validateAndUpdate()
        }

        binding.btnBatal.setOnClickListener {
            setEditMode(false)
            currentBarang?.let { populateLabels(it) }
        }

        binding.btnHapus.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    // ══════════════════════════════════════════════════════
    // OBSERVE
    // ══════════════════════════════════════════════════════

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSimpan.isEnabled    = !loading
            binding.btnHapus.isEnabled     = !loading
        }

        viewModel.selectedBarang.observe(this) { barang ->
            barang?.let {
                currentBarang = it
                populateLabels(it)
            }
        }

        viewModel.errorMessage.observe(this) { msg ->
            msg?.let {
                showSnackbar(it, isError = true)
                viewModel.clearError()
            }
        }

        viewModel.isDone.observe(this) { done ->
            if (done) {
                viewModel.clearDone()
                finish()
            }
        }
    }

    // ══════════════════════════════════════════════════════
    // UI HELPERS
    // ══════════════════════════════════════════════════════

    // Isi label di hero card dan info card (mode detail)
    private fun populateLabels(barang: Barang) {
        binding.tvIconLetter.text    = barang.namaBarang.firstOrNull()?.uppercaseChar()?.toString() ?: "B"
        binding.tvNamaBarangLabel.text = barang.namaBarang
        binding.tvKategoriLabel.text   = barang.kategori
        binding.tvHargaLabel.text      = barang.hargaFormatted()
        binding.tvStokLabel.text       = "${barang.stok} pcs"
        binding.tvDeskripsiLabel.text  = barang.deskripsi.ifEmpty { "-" }

        // Isi form edit juga (siap pakai saat tombol Edit ditekan)
        binding.etNamaBarang.setText(barang.namaBarang)
        binding.actvKategori.setText(barang.kategori, false)
        binding.etStok.setText(barang.stok.toString())
        binding.etHarga.setText(barang.harga.toString())
        binding.etDeskripsi.setText(barang.deskripsi)
    }

    private fun setEditMode(edit: Boolean) {
        // Tampilkan/sembunyikan card dan tombol
        binding.cardHero.visibility  = if (edit) View.GONE  else View.VISIBLE
        binding.cardInfo.visibility  = if (edit) View.GONE  else View.VISIBLE
        binding.cardEdit.visibility  = if (edit) View.VISIBLE else View.GONE

        binding.btnEdit.visibility   = if (edit) View.GONE  else View.VISIBLE
        binding.btnHapus.visibility  = if (edit) View.GONE  else View.VISIBLE
        binding.btnSimpan.visibility = if (edit) View.VISIBLE else View.GONE
        binding.btnBatal.visibility  = if (edit) View.VISIBLE else View.GONE
    }

    private fun validateAndUpdate() {
        val nama      = binding.etNamaBarang.text.toString().trim()
        val kategori  = binding.actvKategori.text.toString().trim()
        val stokStr   = binding.etStok.text.toString().trim()
        val hargaStr  = binding.etHarga.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()

        binding.tilNamaBarang.error = null
        binding.tilKategori.error   = null
        binding.tilStok.error       = null
        binding.tilHarga.error      = null

        var isValid = true
        if (nama.isEmpty())     { binding.tilNamaBarang.error = "Nama barang wajib diisi"; isValid = false }
        if (kategori.isEmpty()) { binding.tilKategori.error   = "Kategori wajib dipilih";  isValid = false }
        if (stokStr.isEmpty())  { binding.tilStok.error       = "Stok wajib diisi";        isValid = false }
        if (hargaStr.isEmpty()) { binding.tilHarga.error      = "Harga wajib diisi";       isValid = false }
        if (!isValid) return

        val updated = Barang(
            id         = barangId,
            namaBarang = nama,
            kategori   = kategori,
            stok       = stokStr.toIntOrNull() ?: 0,
            harga      = hargaStr.toIntOrNull() ?: 0,
            deskripsi  = deskripsi
        )
        viewModel.updateBarang(updated)
        setResult(Constants.RESULT_UPDATED)
    }

    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Barang")
            .setMessage("Yakin ingin menghapus \"${currentBarang?.namaBarang}\"?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteBarang(barangId)
                setResult(Constants.RESULT_DELETED)
            }
            .show()
    }

    private fun showSnackbar(message: String, isError: Boolean) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(
            getColor(if (isError) R.color.danger else R.color.success)
        )
        snackbar.setTextColor(getColor(R.color.text_on_primary))
        snackbar.show()
    }
}