package com.empresateste.webservicephp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements SearchView.OnQueryTextListener {

    //ProgressDialog
    private ProgressDialog progressDialog;

    //SearchView
    private SearchView searchView;
    private ListView listView;

    //Creating JSONParser Object;
    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> produtoList;

    //Url to GET all product list
    private static String url_all_produtos = "http://gse7.xyz/dulu/get_all.php";

    //JSON node names
    private static final String TAG_SUCESSO = "sucesso";
    private static final String TAG_PRODUTOS = "tb_usuario";
    private static final String TAG_ID = "id";
    private static final String TAG_NOME = "nome";
    private static final String TAG_TEL = "telefone";


    //product JSONArray
    JSONArray produtos = null;

    ListView lista;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //HashMap para o ListView
        produtoList = new ArrayList<HashMap<String, String>>();

        //carregar os produtos no background thread
        new LoadAllProducts().execute();
        lista = (ListView)findViewById(R.id.listAllProdutos);

        searchView = (SearchView)findViewById(R.id.search_view);
        listView = (ListView)findViewById(R.id.listView);
        lista.setTextFilterEnabled(true);
        setupSearchView();
    }
    private void setupSearchView(){
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Busca Rápida");
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        if(TextUtils.isEmpty(newText)){
            lista.clearTextFilter();
        }else{
            lista.setFilterText(newText.toString());
        }
        return true;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }
    class LoadAllProducts extends AsyncTask<String, String, String>{
        //Antes de comecar o background thread show progressDialog
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Carregando produtos... Por favor aguarde...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        //Obtendo a lista de todos os produtos

        protected String doInBackground(String... args){

            //Building params
            List params = new ArrayList();
            //Getting JSON String from URL
            JSONObject json = jsonParser.makeHttpRequest(url_all_produtos, "GET", params);

            //Check your log cat for JSON response
            Log.d("Todas as empresas: ", json.toString());

            try {
                //cheking for SUCCESS TAG
                int sucesso = json.getInt(TAG_SUCESSO);

                if(sucesso == 1){
                    //products found
                    //get array dos produtos
                    produtos = json.getJSONArray(TAG_PRODUTOS);

                    //Looping all produtos

                    for (int i = 0; i < produtos.length(); i++){
                        JSONObject c = produtos.getJSONObject(i);

                        //Storing  sach json item in variable
                        String id = c.getString(TAG_ID);
                        String nome = c.getString(TAG_NOME);
                        String telefone = c.getString(TAG_TEL);

                        //Creating new hashMap
                        HashMap map = new HashMap();

                        //adding sach child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_NOME, nome);
                        map.put(TAG_TEL, telefone);

                        produtoList.add(map);

                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        //Depois de completar o backgound finaliza dimiss progress dialog

        protected void onPostExecute(String file_url){

            //fecha o dialogo depois de pegar os produtos
            progressDialog.dismiss();
            //updating UI from obackgound tread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Uptade parded JSON into ListView
                    ListAdapter adapter = new SimpleAdapter(
                            MainActivity.this,
                            produtoList,
                            R.layout.single_post,


                            new String[]{
                                    TAG_ID,
                                    TAG_NOME,
                                    TAG_TEL,

                            },

                            new int[]{
                                    R.id.single_post_tv_id,
                                    R.id.single_post_tv_nome,
                                    R.id.single_post_tv_tel,
                            }
                    );

                    //update list View
                    //setListAdapter(adapter)
                    lista.setAdapter(adapter);
                }
            });

        }

    }

}
