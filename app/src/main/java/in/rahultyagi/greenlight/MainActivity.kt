package `in`.rahultyagi.greenlight

import `in`.rahultyagi.greenlight.R
import `in`.rahultyagi.greenlight.model
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var dataAdapter: MyCustomAdapter? = null

    private var editsearch: SearchView? = null
    internal var productList = ArrayList<model>()

    internal var productQtyData = "0"
    var unitAndRevenu_Hashmap = HashMap<String, String>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editsearch = findViewById(R.id.searchtab)
        editsearch!!.setOnQueryTextListener(this)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        unitAndRevenu_Hashmap.clear()
        productList()

    }

    override fun onQueryTextSubmit(query: String): Boolean {
        dataAdapter!!.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        dataAdapter!!.filter(newText)
        return false
    }


    fun order(view: View) {

        Toast.makeText(this, "output " + unitAndRevenu_Hashmap, Toast.LENGTH_LONG).show()
    }

    private inner class MyCustomAdapter(
        context: Context, textViewResourceId: Int,
        productList: ArrayList<model>
    ) : ArrayAdapter<model>(context, textViewResourceId, productList) {

        private var productList = ArrayList<model>()
        private val arraylist: ArrayList<model>

        init {
            this.productList = productList
            this.arraylist = ArrayList()
            this.arraylist.addAll(productList)
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            var view = view

            val model = productList[position]

            if (view == null) {
                val vi = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = vi.inflate(R.layout.product_list, null)
                val quantity = view!!.findViewById(R.id.unit_target_id) as EditText
                val revenuTarget = view.findViewById(R.id.revenu_target_id) as EditText
                quantity.addTextChangedListener(MyTextWatcher(view))
                revenuTarget.addTextChangedListener(MyTextWatcherRevenu(view))
            }

            val quantity = view.findViewById(R.id.unit_target_id) as EditText
            val revenuTarget = view.findViewById(R.id.revenu_target_id) as EditText

            val description = view.findViewById(R.id.product_name_id) as TextView
            description.text = model.SKU_Name
            val partner = view.findViewById(R.id.partner_id) as TextView
            partner.text = model.partner_name


            quantity.tag = model
            if (model.units_target == 0) {
                quantity.setText("")
            } else {
                quantity.setText(model.units_target.toString())
            }

            revenuTarget.tag = model
            if (model.revenue_targe == 0) {
                revenuTarget.setText("")
            } else {
                revenuTarget.setText(model.revenue_targe.toString())
            }
            return view

        }

        fun filter(charText: String) {
            var charText = charText

            charText = charText.toLowerCase(Locale.getDefault())
            productList.clear()
            if (charText.length == 0) {
                productList.addAll(arraylist)
            } else {
                for (wp in arraylist) {
                    if ((wp.SKU_Name.toLowerCase(Locale.getDefault()).contains(
                            charText
                        ) || wp.partner_name.toLowerCase(Locale.getDefault()).contains(
                            charText
                        ))
                    ) {
                        productList.add(wp)
                    }
                }
            }
            notifyDataSetChanged()
        }


    }


    private inner class MyTextWatcher(private val view: View) : TextWatcher {


        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            val qtyString = s.toString().trim { it <= ' ' }

            var quantity = 0
            try {
                quantity = if (qtyString == "") 0 else Integer.valueOf(qtyString)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

            val qtyView = view.findViewById(R.id.unit_target_id) as EditText
            val model = qtyView.tag as model
            qtyView.setSelection(qtyView.length())

            if (model.units_target != quantity) {

                model.units_target = quantity


                if (model.units_target != 0) {

                    qtyView.setText(model.units_target.toString())
                } else {
                    qtyView.setText("")
                }


                /*    if (model.units_target === 0) {

                        unitAndRevenu_Hashmap.remove(model.sku_id)

                    } else {
                        unitAndRevenu_Hashmap[model.partner_name] =
                            model.units_target.toString() + "&&" + model.SKU_Name + "&&" + model.sku_id + "&&" + model.revenue_targe
                        Log.e("jhsdfhg", (unitAndRevenu_Hashmap).toString())

                    }
    */

                if (model.units_target > 0) {

                    productQtyData = model.units_target.toString()
                } else {


                }


            }

            return
        }


    }


    private inner class MyTextWatcherRevenu(private val view: View) : TextWatcher {


        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {


            val qtyString = s.toString().trim { it <= ' ' }

            var revenuQuantity = 0
            try {
                revenuQuantity = if (qtyString == "") 0 else Integer.valueOf(qtyString)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

            val revenuTarget = view.findViewById(R.id.revenu_target_id) as EditText
            val model = revenuTarget.tag as model
            revenuTarget.setSelection(revenuTarget.length())

            if (model.revenue_targe != revenuQuantity) {

                model.revenue_targe = revenuQuantity

                if (model.revenue_targe != 0) {

                    revenuTarget.setText(model.revenue_targe.toString())
                } else {
                    revenuTarget.setText("")
                }
                if (model.revenue_targe == 0) {

                    unitAndRevenu_Hashmap.remove(model.partner_name+"="+model.sku_id)

                } else {
                    unitAndRevenu_Hashmap[model.partner_name+"="+model.sku_id] =
                        model.units_target.toString() + "&&" + model.SKU_Name + "&&" + model.sku_id + "&&" + model.revenue_targe

                }

                if (model.revenue_targe > 0) {

                    productQtyData = model.revenue_targe.toString()
                } else {

                }

            }

            return
        }
    }

    private fun productList() {
        AndroidNetworking.get("http://rahultyagi.in/jsondata")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {

                    try {
                        val array = response.getJSONArray("partners")

                        for (i in 0 until array.length()) {

                            val jsonobject = array.getJSONObject(i)
                            val partnerName = jsonobject.getString("partner_name")
                            val products = jsonobject.getJSONArray("products")
                            for (j in 0 until products.length()) {
                                val productJson = products.getJSONObject(j)
                                val productName = productJson.getString("SKU_Name")
                                val productId = productJson.getString("sku_id")

                                val model = model(
                                    partnerName, productName, productId
                                )

                                productList.add(model)
                            }

                            dataAdapter = MyCustomAdapter(
                                this@MainActivity,
                                R.layout.product_list,
                                productList
                            )
                            val listView = findViewById<ListView>(R.id.listView)
                            listView.adapter = dataAdapter
                            listView.invalidateViews()
                            dataAdapter!!.notifyDataSetChanged()

                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                override fun onError(anError: ANError) {

                }
            })

    }

 /*   companion object {
        var unitAndRevenu_Hashmap = HashMap<String, String>()
    }*/


}

