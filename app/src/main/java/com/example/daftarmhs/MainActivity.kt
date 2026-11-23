package com.example.daftarmhs

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.daftarmhs.data.ApiClient
import com.example.daftarmhs.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchMahasiswaList()

        binding.btnSemua.setOnClickListener {
            fetchMahasiswaList()
        }

        binding.btnDetail.setOnClickListener {
            val id = validateInputId() ?: return@setOnClickListener
            fetchDetailMahasiswa(id)
        }

        binding.btnTugas.setOnClickListener {
            val id = validateInputId() ?: return@setOnClickListener
            fetchTugasMahasiswa(id)
        }
    }

    private fun validateInputId(): Int? {
        val inputIdStr = binding.etUserId.text.toString().trim()
        if (inputIdStr.isEmpty()) {
            Toast.makeText(this, "Silakan masukkan ID mahasiswa (1-10)", Toast.LENGTH_SHORT).show()
            binding.tvResult.text = "Error: ID tidak boleh kosong!"
            return null
        }

        val id = inputIdStr.toIntOrNull()
        if (id == null || id !in 1..10) {
            Toast.makeText(this, "ID harus berupa angka antara 1 sampai 10", Toast.LENGTH_SHORT).show()
            binding.tvResult.text = "Error: ID harus antara 1-10!"
            return null
        }

        return id
    }

    private fun fetchMahasiswaList() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvResult.text = ""

        lifecycleScope.launch {
            try {
                val users = withContext(Dispatchers.IO) {
                    ApiClient.instance.getUsers()
                }
                Log.d("API", "jumlah : ${users.size}")

                val builder = StringBuilder()
                users.forEachIndexed { index, mahasiswa ->
                    builder.append("${index + 1}. ${mahasiswa.name} (${mahasiswa.email})\n")
                }

                binding.tvResult.text = builder.toString().trimEnd()
            } catch (e: Exception) {
                Log.e("API", "Error loading users", e)
                binding.tvResult.text = "Gagal : ${e.localizedMessage ?: e.message ?: "Koneksi Error"}"
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun fetchDetailMahasiswa(userId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvResult.text = ""

        lifecycleScope.launch {
            try {
                val detail = withContext(Dispatchers.IO) {
                    ApiClient.instance.getDetail(userId)
                }

                val builder = StringBuilder()
                builder.append("=== DETAIL MAHASISWA (B1) ===\n")
                builder.append("ID       : ${detail.id}\n")
                builder.append("Nama     : ${detail.name}\n")
                builder.append("Username : ${detail.username}\n")
                builder.append("Email    : ${detail.email}\n")
                if (!detail.phone.isNullOrEmpty()) {
                    builder.append("Telepon  : ${detail.phone}\n")
                }

                binding.tvResult.text = builder.toString().trimEnd()
            } catch (e: Exception) {
                Log.e("API", "Error loading detail", e)
                binding.tvResult.text = "Gagal : ${e.localizedMessage ?: e.message ?: "Koneksi Error"}"
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun fetchTugasMahasiswa(userId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvResult.text = ""

        lifecycleScope.launch {
            try {
                val tugasList = withContext(Dispatchers.IO) {
                    ApiClient.instance.getTugas(userId)
                }

                val builder = StringBuilder()
                builder.append("=== DAFTAR TUGAS MAHASISWA (B2) ===\n")
                builder.append("Total: ${tugasList.size} Tugas\n\n")
                if (tugasList.isEmpty()) {
                    builder.append("Belum ada tugas.\n")
                } else {
                    tugasList.forEachIndexed { index, tugas ->
                        val status = if (tugas.completed) "✓ Selesai" else "✗ Belum"
                        builder.append("${index + 1}. [$status] ${tugas.title}\n")
                    }
                }

                binding.tvResult.text = builder.toString().trimEnd()
            } catch (e: Exception) {
                Log.e("API", "Error loading tugas", e)
                binding.tvResult.text = "Gagal : ${e.localizedMessage ?: e.message ?: "Koneksi Error"}"
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
