package com.example.loketkereta

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class BookingActivity : AppCompatActivity() {
    private val hargaTiketPerKelas = intArrayOf(0, 100000, 150000, 200000, 250000, 300000)

    private val hargaTambahanStasiun = arrayOf(
        intArrayOf(0, 50000, 100000, 150000, 200000, 250000, 300000, 350000, 400000, 450000, 500000, 550000, 600000, 650000, 700000),
        intArrayOf(50000, 0, 75000, 125000, 175000, 225000, 275000, 325000, 375000, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(100000, 75000, 0, 125000, 175000, 225000, 275000, 325000, 375000, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(150000, 125000, 75000, 0, 175000, 225000, 275000, 325000, 375000, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(200000, 175000, 125000, 75000, 0, 225000, 275000, 325000, 375000, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(250000, 225000, 175000, 125000, 75000, 0, 275000, 325000, 375000, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(300000, 275000, 225000, 175000, 125000, 75000, 0, 325000, 375000, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(350000, 325000, 275000, 225000, 175000, 125000, 75000, 0, 375000, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(400000, 375000, 325000, 275000, 225000, 175000, 125000, 75000, 0, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(450000, 425000, 375000, 325000, 275000, 225000, 175000, 125000, 75000, 0, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(500000, 475000, 425000, 375000, 325000, 275000, 225000, 175000, 125000, 75000, 0, 525000, 575000, 625000, 675000),
        intArrayOf(550000, 525000, 475000, 425000, 375000, 325000, 275000, 225000, 175000, 125000, 75000, 0, 575000, 625000, 675000),
        intArrayOf(600000, 575000, 525000, 475000, 425000, 375000, 325000, 275000, 225000, 175000, 125000, 75000, 0, 625000, 675000),
        intArrayOf(650000, 625000, 575000, 525000, 475000, 425000, 375000, 325000, 375000, 425000, 475000, 525000, 575000, 625000, 675000),
        intArrayOf(700000, 675000, 625000, 575000, 525000, 475000, 425000, 375000, 325000, 275000, 225000, 175000, 125000, 75000, 0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val stasiunArray = resources.getStringArray(R.array.stasiun_kereta)
        val kelasArray = resources.getStringArray(R.array.kelas_kereta)

        val spinnerBerangkat = findViewById<Spinner>(R.id.spinner_berangkat)
        val spinnerTujuan = findViewById<Spinner>(R.id.spinner_tujuan)
        val spinnerKelas = findViewById<Spinner>(R.id.spinner_kelas)
        val totalHargaTextView = findViewById<TextView>(R.id.totalHarga)
        val jumlahAnakTextView = findViewById<TextView>(R.id.textJumlahAnak)
        val jumlahDewasaTextView = findViewById<TextView>(R.id.textJumlahDewasa)

        val anakMinusButton = findViewById<ImageView>(R.id.minus1)
        val anakPlusButton = findViewById<ImageView>(R.id.add1)
        val dewasaMinusButton = findViewById<ImageView>(R.id.minus2)
        val dewasaPlusButton = findViewById<ImageView>(R.id.add2)

        val stasiunPlaceholder = "Pilih stasiun"
        val stasiunList = mutableListOf(stasiunPlaceholder)
        stasiunList.addAll(stasiunArray)
        val stasiunAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stasiunList.toTypedArray())

        val kelasPlaceholder = "Pilih kelas"
        val kelasList = mutableListOf(kelasPlaceholder)
        kelasList.addAll(kelasArray)
        val kelasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kelasList.toTypedArray())
        kelasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKelas.adapter = kelasAdapter

        stasiunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kelasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerBerangkat.adapter = stasiunAdapter
        spinnerTujuan.adapter = stasiunAdapter
        spinnerKelas.adapter = kelasAdapter

        spinnerBerangkat.prompt = stasiunPlaceholder
        spinnerTujuan.prompt = stasiunPlaceholder
        spinnerKelas.prompt = kelasPlaceholder

        var jumlahAnak = 0
        var jumlahDewasa = 0
        var kelasTerpilih = 0
        var stasiunAwalTerpilih = -1
        var stasiunAkhirTerpilih = -1

        val inputTanggal = findViewById<TextInputEditText>(R.id.inputTanggal)
        inputTanggal.setOnClickListener {
            showDatePickerDialog()
        }


        val chipGroup = findViewById<ChipGroup>(R.id.chip_group)

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedChips = group.checkedChipIds.map { chipId ->
                group.findViewById<Chip>(chipId)
            }

            val additionalPrice = calculateAdditionalPrice(selectedChips)
            updateTotalHarga(jumlahAnak, jumlahDewasa, kelasTerpilih, stasiunAwalTerpilih, stasiunAkhirTerpilih, totalHargaTextView, additionalPrice)
        }

        spinnerKelas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                kelasTerpilih = position
                updateTotalHarga(jumlahAnak, jumlahDewasa, kelasTerpilih, stasiunAwalTerpilih, stasiunAkhirTerpilih, totalHargaTextView)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        spinnerBerangkat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stasiunAwalTerpilih = position
                if (stasiunAwalTerpilih == stasiunAkhirTerpilih) {
                    Toast.makeText(applicationContext, "Stasiun asal dan tujuan tidak boleh sama.", Toast.LENGTH_SHORT).show()
                    spinnerBerangkat.setSelection(0)
                } else {
                    updateTotalHarga(jumlahAnak, jumlahDewasa, kelasTerpilih, stasiunAwalTerpilih, stasiunAkhirTerpilih, totalHargaTextView)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        spinnerTujuan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stasiunAkhirTerpilih = position
                if (stasiunAwalTerpilih == stasiunAkhirTerpilih) {
                    Toast.makeText(applicationContext, "Stasiun asal dan tujuan tidak boleh sama.", Toast.LENGTH_SHORT).show()
                    spinnerTujuan.setSelection(0)
                } else {
                    updateTotalHarga(jumlahAnak, jumlahDewasa, kelasTerpilih, stasiunAwalTerpilih, stasiunAkhirTerpilih, totalHargaTextView)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


        anakMinusButton.setOnClickListener {
            if (jumlahAnak > 0) {
                jumlahAnak--
                jumlahAnakTextView.text = jumlahAnak.toString()
                updateTotalHarga(jumlahAnak, jumlahDewasa, kelasTerpilih, stasiunAwalTerpilih, stasiunAkhirTerpilih, totalHargaTextView)
            }
        }

        anakPlusButton.setOnClickListener {
            jumlahAnak++
            jumlahAnakTextView.text = jumlahAnak.toString()
            updateTotalHarga(jumlahAnak, jumlahDewasa, kelasTerpilih, stasiunAwalTerpilih, stasiunAkhirTerpilih, totalHargaTextView)
        }

        dewasaMinusButton.setOnClickListener {
            if (jumlahDewasa > 0) {
                jumlahDewasa--
                jumlahDewasaTextView.text = jumlahDewasa.toString()
                updateTotalHarga(jumlahAnak, jumlahDewasa, kelasTerpilih, stasiunAwalTerpilih, stasiunAkhirTerpilih, totalHargaTextView)
            }
        }

        dewasaPlusButton.setOnClickListener {
            jumlahDewasa++
            jumlahDewasaTextView.text = jumlahDewasa.toString()
            updateTotalHarga(jumlahAnak, jumlahDewasa, kelasTerpilih, stasiunAwalTerpilih, stasiunAkhirTerpilih, totalHargaTextView)
        }

        val btnBooking = findViewById<MaterialButton>(R.id.btnBooking)

        btnBooking.setOnClickListener {
            val selectedDate = inputTanggal.text.toString()
            val stasiunAsal = spinnerBerangkat.selectedItem.toString()
            val stasiunTujuan = spinnerTujuan.selectedItem.toString()

            if (selectedDate.isNotEmpty() && stasiunAsal != "Pilih stasiun" && stasiunTujuan != "Pilih stasiun") {
                val intent = Intent(this, DashboardActivity::class.java)
                val tanggal = intent.getStringExtra("tanggal")
                intent.putExtra("tanggal", selectedDate)
                intent.putExtra("stasiunAsal", stasiunAsal)
                intent.putExtra("stasiunTujuan", stasiunTujuan)

                val selectedChips = chipGroup.checkedChipIds.map { chipId ->
                    chipGroup.findViewById<Chip>(chipId).text.toString()
                }
                intent.putStringArrayListExtra("paketTambahan", ArrayList(selectedChips))


                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Lengkapi semua data sebelum pesan perjalanan.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            val inputTanggal = findViewById<TextInputEditText>(R.id.inputTanggal)
            inputTanggal.setText(selectedDate)
        }, year, month, day)
        datePickerDialog.show()
    }


    private fun calculateAdditionalPrice(selectedChips: List<Chip>): Int {
        var additionalPrice = 0

        for (chip in selectedChips) {
            when (chip.id) {
                R.id.chip1 -> additionalPrice += 50000
                R.id.chip2 -> additionalPrice += 25000
                R.id.chip3 -> additionalPrice += 10000
                R.id.chip4 -> additionalPrice += 5000
                R.id.chip5 -> additionalPrice += 1000
                R.id.chip6 -> additionalPrice += 500
                R.id.chip7 -> additionalPrice += 100
            }
        }

        return additionalPrice
    }


    private fun updateTotalHarga(jumlahAnak: Int, jumlahDewasa: Int, kelas: Int, stasiunAwal: Int, stasiunAkhir: Int, totalHargaTextView: TextView, additionalPrice: Int = 0) {
        if (kelas >= 0 && stasiunAwal >= 0 && stasiunAkhir >= 0 && stasiunAwal != stasiunAkhir && (jumlahAnak > 0 || jumlahDewasa > 0)) {
            val hargaTiket = hargaTiketPerKelas[kelas]
            val hargaTambahan = hargaTambahanStasiun[stasiunAwal][stasiunAkhir]

            val hargaAnak = hargaTiket / 2

            val totalHarga = (hargaTiket * jumlahDewasa + hargaAnak * jumlahAnak + additionalPrice)

            totalHargaTextView.text = "Rp $totalHarga"
        } else {
            totalHargaTextView.text = "Rp 0"
        }
    }
}