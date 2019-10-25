package mx.edu.ittepic.tpdm_u3_practica1_15401055


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    var descripcion : EditText?=null
    var monto : EditText?=null
    var fecha : EditText?=null
    var pagos  : RadioButton?=null
    var insertar : Button?=null
    var lista : ListView?=null
    //declarando objetos de firebase
    var baseRemota = FirebaseFirestore.getInstance()
    var registros= ArrayList<String>()
    var keys     = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        descripcion = findViewById(R.id.descripcion)
        monto       = findViewById(R.id.monto)
        fecha       = findViewById(R.id.fechav)
        pagos        = findViewById(R.id.pagado)
        insertar    = findViewById(R.id.insertar)
        lista       = findViewById(R.id.lista)

        insertar?.setOnClickListener{
            var datosInsertar = hashMapOf(
                "Descripcion" to descripcion?.text.toString(),
                "Monto"       to monto?.text.toString().toDouble(),
                "Fecha"       to fecha?.text.toString(),
                "Pagos"        to pagos?.isChecked.toString().toBoolean()

            )

            baseRemota.collection("RECIBOPAGOS")
                .add(datosInsertar)
                .addOnSuccessListener {
                    Toast.makeText(this,"se inserto con exito",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Fallo al insertar",Toast.LENGTH_SHORT).show()
                }
            limpiarCampos()
        }
        baseRemota.collection("RECIBOPAGOS")
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "no hay acceso a datos", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                registros.clear()
                keys.clear()
                for(document in querySnapshot!!){
                    var cadena = "${document.getString("Descripcion")}\n${"Monto: "+document.getDouble("Monto")}\n${"Fecha: "+document.getString("Fecha")}\n${"Estado Pago: "+document.getBoolean("Pagos")}"
                    registros.add(cadena)
                    keys.add(document.id)
                }
                if(registros.size==0){
                    registros.add("No hay datos capturados")
                }
                var adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,registros)
                lista?.adapter = adapter
            }
        lista?.setOnItemClickListener { parent, view, position, id ->
            AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage("¿QUE DESEAS HACER CON \n ${registros.get(position).toUpperCase()}?")
                .setPositiveButton("Eliminar"){dialog,wich ->
                    baseRemota.collection("RECIBOPAGOS").document(keys.get(position))
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this,"SE PUDO ELIMINAR",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"NO PUDO ELIMINAR",Toast.LENGTH_SHORT).show()
                        }
                }//18 DE OCTUBRE
                .setNegativeButton("Actualizar"){dialog,wich ->
                    var nuevaVentana = Intent(this, Main2Activity::class.java)

                    nuevaVentana.putExtra("id",keys.get(position))
                    startActivity(nuevaVentana)
                    //18 DE OCTUBRE
                }
                .setNeutralButton("Cancelar"){dialog,wich ->}
                .show()
        }
    }
    fun limpiarCampos(){
        descripcion?.setText("");monto?.setText("");fecha?.setText("");
    }
}
