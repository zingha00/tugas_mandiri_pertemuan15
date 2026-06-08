package com.utama.i_mo.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.utama.i_mo.R
import com.utama.i_mo.databinding.ActivityMainBinding
import com.utama.i_mo.model.Barang
import com.utama.i_mo.repository.BarangRepository
import com.utama.i_mo.ui.add.AddBarangActivity
import com.utama.i_mo.ui.detail.DetailBarangActivity
import com.utama.i_mo.utils.Constants
import com.utama.i_mo.viewmodel.BarangViewModel
import com.utama.i_mo.viewmodel.BarangViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: BarangViewModel
    private lateinit var adapter: BarangAdapter
    private var allBarangList = listOf<Barang>()

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Constants.RESULT_ADDED,
            Constants.RESULT_UPDATED,
            Constants.RESULT_DELETED -> viewModel.loadAllBarang()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupSearch()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
    }

    private fun setupViewModel() {
        val factory = BarangViewModelFactory(BarangRepository())
        viewModel = ViewModelProvider(this, factory)[BarangViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = BarangAdapter { barang -> openDetail(barang) }
        binding.rvBarang.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(false)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBarang(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.fabTambah.setOnClickListener {
            activityLauncher.launch(Intent(this, AddBarangActivity::class.java))
        }
        binding.btnRefresh.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.loadAllBarang()
        }
    }

    private fun observeViewModel() {
        viewModel.barangList.observe(this) { list ->
            allBarangList = list
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                filterBarang(query)
            } else {
                adapter.submitList(list)
                updateSummary(list.size)
                showEmptyState(list.isEmpty())
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { msg ->
            msg?.let {
                showSnackbar(it, isError = true)
                viewModel.clearError()
            }
        }

        viewModel.successMessage.observe(this) { msg ->
            msg?.let {
                showSnackbar(it, isError = false)
                viewModel.clearSuccess()
            }
        }
    }

    private fun filterBarang(query: String) {
        val filtered = if (query.isEmpty()) {
            allBarangList
        } else {
            allBarangList.filter {
                it.namaBarang.contains(query, ignoreCase = true) ||
                        it.kategori.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)
        updateSummary(filtered.size)
        showEmptyState(filtered.isEmpty())
    }

    private fun updateSummary(count: Int) {
        binding.tvTotalBarang.text = count.toString()
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvBarang.visibility    = if (isEmpty) View.GONE   else View.VISIBLE
    }

    private fun openDetail(barang: Barang) {
        val intent = Intent(this, DetailBarangActivity::class.java).apply {
            putExtra(Constants.KEY_BARANG_ID, barang.id)
        }
        activityLauncher.launch(intent)
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