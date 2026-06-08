package com.utama.i_mo.ui.add

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.utama.i_mo.R
import com.utama.i_mo.databinding.ActivityAddBarangBinding
import com.utama.i_mo.model.Barang
import com.utama.i_mo.repository.BarangRepository
import com.utama.i_mo.utils.Constants
import com.utama.i_mo.viewmodel.BarangViewModel
import com.utama.i_mo.viewmodel.BarangViewModelFactory
import com.google.android.material.snackbar.Snackbar

class AddBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBarangBinding
    private lateinit var viewModel: BarangViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupKategoriDropdown()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.title_tambah_barang)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViewModel() {
        val factory = BarangViewModelFactory(BarangRepository())
        viewModel = ViewModelProvider(this, factory)[BarangViewModel::class.java]
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
        binding.btnSimpan.setOnClickListener { validateAndSave() }
        binding.btnBatal.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { loading ->
            binding.btnSimpan.isEnabled = !loading
            binding.btnBatal.isEnabled  = !loading
            binding.btnSimpan.text = if (loading)
                "Menyimpan..." else getString(R.string.btn_simpan)
        }

        viewModel.errorMessage.observe(this) { msg ->
            msg?.let {
                showSnackbar(it, isError = true)
                viewModel.clearError()
            }
        }

        viewModel.isDone.observe(this) { done ->
            if (done) {
                setResult(Constants.RESULT_ADDED)
                viewModel.clearDone()
                finish()
            }
        }
    }

    private fun validateAndSave() {
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

        if (nama.isEmpty()) {
            binding.tilNamaBarang.error = "Nama barang wajib diisi"
            isValid = false
        }
        if (kategori.isEmpty()) {
            binding.tilKategori.error = "Kategori wajib dipilih"
            isValid = false
        }
        if (stokStr.isEmpty()) {
            binding.tilStok.error = "Stok wajib diisi"
            isValid = false
        }
        if (hargaStr.isEmpty()) {
            binding.tilHarga.error = "Harga wajib diisi"
            isValid = false
        }

        if (!isValid) return

        val barang = Barang(
            namaBarang = nama,
            kategori   = kategori,
            stok       = stokStr.toIntOrNull() ?: 0,
            harga      = hargaStr.toIntOrNull() ?: 0,
            deskripsi  = deskripsi
        )

        viewModel.createBarang(barang)
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