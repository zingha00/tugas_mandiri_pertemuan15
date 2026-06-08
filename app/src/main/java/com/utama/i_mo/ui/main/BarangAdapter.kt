package com.utama.i_mo.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.utama.i_mo.databinding.ItemBarangBinding
import com.utama.i_mo.model.Barang
import com.utama.i_mo.model.StokStatus

class BarangAdapter(
    private val onClick: (Barang) -> Unit
) : ListAdapter<Barang, BarangAdapter.BarangViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Barang>() {
        override fun areItemsTheSame(oldItem: Barang, newItem: Barang) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Barang, newItem: Barang) =
            oldItem == newItem
    }

    inner class BarangViewHolder(
        private val binding: ItemBarangBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(barang: Barang) {
            binding.apply {

                // Huruf pertama nama barang sebagai icon
                tvIconLetter.text = barang.namaBarang
                    .firstOrNull()?.uppercaseChar()?.toString() ?: "B"

                tvNamaBarang.text = barang.namaBarang
                tvKategori.text   = barang.kategori
                tvStok.text       = "Stok: ${barang.stok} pcs"
                tvHarga.text      = barang.hargaFormatted()

                // Warna badge kategori
                val (bgColor, textColor) = when (barang.kategori.lowercase()) {
                    "elektronik" -> Pair(
                        root.context.getColor(com.utama.i_mo.R.color.badge_elektronik),
                        root.context.getColor(com.utama.i_mo.R.color.badge_elektronik_text)
                    )
                    "aksesoris" -> Pair(
                        root.context.getColor(com.utama.i_mo.R.color.badge_aksesoris),
                        root.context.getColor(com.utama.i_mo.R.color.badge_aksesoris_text)
                    )
                    else -> Pair(
                        root.context.getColor(com.utama.i_mo.R.color.badge_default),
                        root.context.getColor(com.utama.i_mo.R.color.badge_default_text)
                    )
                }
                tvKategori.backgroundTintList = android.content.res.ColorStateList.valueOf(bgColor)
                tvKategori.setTextColor(textColor)

                // Warna icon box sesuai status stok
                val iconBgColor = when (barang.statusStok()) {
                    StokStatus.TERSEDIA -> root.context.getColor(com.utama.i_mo.R.color.badge_aksesoris)
                    StokStatus.MENIPIS  -> root.context.getColor(com.utama.i_mo.R.color.warning_light)
                    StokStatus.HABIS    -> root.context.getColor(com.utama.i_mo.R.color.danger_light)
                }
                tvIconLetter.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                (tvIconLetter.parent as? android.view.View)?.backgroundTintList = 
                    android.content.res.ColorStateList.valueOf(iconBgColor)

                // Klik item → buka detail
                root.setOnClickListener { onClick(barang) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val binding = ItemBarangBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BarangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}