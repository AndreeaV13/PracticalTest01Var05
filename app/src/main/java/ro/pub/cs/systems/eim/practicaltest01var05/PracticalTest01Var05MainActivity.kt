package ro.pub.cs.systems.eim.practicaltest01var05

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PracticalTest01Var05MainActivity : AppCompatActivity() {

    private lateinit var buttonsText: EditText

    // B.2.a - numarul total de apasari ale butoanelor (neasociat unui control din UI)
    private var pressCount = 0

    // C.2 - la revenirea din activitatea secundara: Toast cu rezultatul,
    // apoi reinitializarea campului text si a numarului de apasari
    private val secondaryActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val message = if (result.resultCode == RESULT_OK) "Verify" else "Cancel"
            Toast.makeText(this, "Result: $message", Toast.LENGTH_LONG).show()
            buttonsText.setText("")
            pressCount = 0
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test01_var05_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonsText = findViewById(R.id.buttons_text)

        // B.2.b - restaurarea valorii la pornirea activitatii (daca a fost salvata)
        // si afisarea ei intr-un Toast
        if (savedInstanceState != null && savedInstanceState.containsKey(PRESS_COUNT_KEY)) {
            pressCount = savedInstanceState.getInt(PRESS_COUNT_KEY)
            Log.d(TAG, "onCreate: restored pressCount = $pressCount")
            Toast.makeText(this, "Total button presses: $pressCount", Toast.LENGTH_LONG).show()
        }

        // B.1 - un singur ascultator pentru cele 5 butoane:
        // textul butonului apasat este concatenat in campul text, separat prin virgula
        val cornerButtonListener = { view: View ->
            pressCount++
            val buttonText = (view as Button).text.toString()
            val current = buttonsText.text.toString()
            buttonsText.setText(if (current.isEmpty()) buttonText else "$current, $buttonText")
        }

        listOf(
            R.id.top_left_button,
            R.id.top_right_button,
            R.id.center_button,
            R.id.bottom_left_button,
            R.id.bottom_right_button
        ).forEach { id -> findViewById<Button>(id).setOnClickListener(cornerButtonListener) }

        // A.2 / C.2 - butonul care invoca activitatea secundara, transmitand sablonul
        findViewById<Button>(R.id.navigate_to_secondary_activity_button).setOnClickListener {
            val intent = Intent(this, PracticalTest01Var05SecondaryActivity::class.java)
            intent.putExtra(TEMPLATE_KEY, buttonsText.text.toString())
            secondaryActivityLauncher.launch(intent)
        }
    }

    // B.2.b - salvarea valorii cand sistemul distruge activitatea
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PRESS_COUNT_KEY, pressCount)
        Log.d(TAG, "onSaveInstanceState: pressCount = $pressCount")
    }

    companion object {
        private const val TAG = "PracticalTest01Var05"
        private const val PRESS_COUNT_KEY = "pressCount"
        const val TEMPLATE_KEY = "template"
    }
}
