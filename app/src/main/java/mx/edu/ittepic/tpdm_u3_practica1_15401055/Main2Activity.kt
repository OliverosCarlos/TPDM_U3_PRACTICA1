package mx.edu.ittepic.tpdm_u3_practica1_15401055

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class Main2Activity : AppCompatActivity() {
    var des : EditText?=null
    var mon : EditText?=null
    var fec : EditText?=null
    var pag : RadioButton?=null
    var actualizar : Button?=null
    var cancelar : Button?=null

    var baseRemota= FirebaseFirestore.getInstance()
    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        des=findViewById(R.id.des)
        mon=findViewById(R.id.mon)
        fec=findViewById(R.id.fec)
        pag=findViewById(R.id.pag)
        actualizar=findViewById(R.id.actualizar)
        cancelar=findViewById(R.id.cancelar)


        id = intent.extras?.getString("id").toString()!!

        baseRemota.collection("RECIBOPAGOS")
            .document(id)
            .get()
            .addOnSuccessListener {
                des?.setText(it.getString("Descripcion"))
                mon?.setText(it.getDouble("Monto").toString())
                fec?.setText(it.getString("Fecha"))
                pag?.isChecked = it.getBoolean("Pagos")!!
            }
            .addOnFailureListener {
                des?.setText("NULL")
                mon?.setText("N")
                fec?.setText("NULL")


                des?.isEnabled = false
                mon?.isEnabled = false
                fec?.isEnabled = false
                actualizar?.isEnabled = false
            }
        actualizar?.setOnClickListener {
            var datosActualizar = hashMapOf(
                "Descripcion" to des?.text.toString(),
                "Monto" to mon?.text.toString().toDouble(),
                "Fecha" to fec?.text.toString(),
                "Pagos" to  pag?.isChecked.toString().toBoolean()
            )
            baseRemota.collection("RECIBOPAGOS")
                .document(id)
                .set(datosActualizar)
                .addOnSuccessListener {
                    limpiarC()
                    Toast.makeText(this,"SE ACTUALIZO", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    Toast.makeText(this,"NO SE ACTUALIZO", Toast.LENGTH_SHORT).show()
                }
        }
        cancelar?.setOnClickListener { finish() }

    }
    fun limpiarC(){
        des?.setText("");mon?.setText("");fec?.setText("")
    }
}
