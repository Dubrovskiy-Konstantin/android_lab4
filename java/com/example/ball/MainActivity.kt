package com.example.ball

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import android.database.Cursor
import android.database.sqlite.*
import android.util.Log


class MainActivity : AppCompatActivity() {
    private val reqestCode = 1
    private lateinit var buttonStart: Button
    private lateinit var buttonRate: Button
    private lateinit var buttonRegister: Button
    private lateinit var enterName : EditText
    private lateinit var imageView: ImageView
    private lateinit var listView: ListView
    private lateinit var choosePlayer: Spinner
    private lateinit var mediaPlayer : MediaPlayer
    private val players = ArrayList<Player>()
    private val playersNicknames = ArrayList<String>()
    private val playersRate = ArrayList<String>()
    private lateinit var defaultNickname : String
    private lateinit var currentPlayer: Player
    private var rateIsClicked : Boolean = false
    private var registerIsClicked : Boolean = false
    private lateinit var dbHandler : DBHandler

    private class DBHandler(val baseContext: Context){
        private val dbName = "ball.db"
        private val tableName = "Players"

        private fun openDB(): SQLiteDatabase{
            return baseContext.openOrCreateDatabase(dbName, MODE_PRIVATE, null)
        }

        private fun makeQuery(sql: String): Boolean{
            val db = openDB()
            try{
                db.execSQL(sql)
                db.close()
                return true
            }
            catch (e: Exception){Log.d("makeQuery", e.message.toString())}
            db.close()
            return false
        }

        fun addToDB(newPlayer: Player): Boolean{
            return makeQuery("INSERT INTO $tableName VALUES ('${newPlayer.Name}', ${newPlayer.Rate}, ${newPlayer.Games});")
        }

        fun rmFromDB(rmPlayer: Player) : Boolean{
            return makeQuery("DELETE FROM $tableName WHERE (Name = '${rmPlayer.Name}');")
        }

        fun updInDB(updPlayer: Player): Boolean{
            return makeQuery("UPDATE $tableName SET Rate = ${updPlayer.Rate}, Games = ${updPlayer.Games} " +
                    "WHERE Name = '${updPlayer.Name}';")
        }

        fun createDB(): Boolean{
            makeQuery("DROP TABLE IF EXISTS $tableName;")
            makeQuery("CREATE TABLE IF NOT EXISTS $tableName (" +
                        "Name TEXT NOT NULL PRIMARY KEY, " +
                        "Rate REAL, " +
                        "Games INTEGER);")
            makeQuery("INSERT INTO $tableName VALUES " +
                        "('Nick', 3700.5, 100), " +
                        "('John', 232.1, 5), " +
                        "('Billy', 420.9, 3), " +
                        "('Ricardo', 419.7, 7);")
            return true
        }

        fun getFromDB() : ArrayList<Player>{
            val db = openDB()
            val players = ArrayList<Player>()
            var query: Cursor? = null
            try{
                query = db.rawQuery("SELECT * FROM $tableName;", null)
                while (query.moveToNext())
                    players.add(Player(query.getString(0), query.getFloat(1), query.getInt(2)))
            }
            catch (e: Exception){Log.d("getFromDB", e.message.toString())}
            query?.close()
            db.close()
            return players
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//----- Remove action bar -----
        try { this.supportActionBar!!.hide() }
        catch (e: NullPointerException) {}
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//----- Views: -----
        setContentView(R.layout.activity_main)
        buttonRegister = findViewById<Button>(R.id.buttonRegister)
        enterName = findViewById<EditText>(R.id.enterName)
        enterName.isVisible = false
        buttonRate = findViewById<Button>(R.id.buttonRate)
        imageView = findViewById<ImageView>(R.id.backgroundImage)
        listView = findViewById<ListView>(R.id.listRate)
        registerForContextMenu(listView)
        listView.isVisible = false
        choosePlayer = findViewById<Spinner>(R.id.choosePlayer)
//----- Fields: -----
        defaultNickname = resources.getString(R.string.choose_player)
        dbHandler = DBHandler(baseContext = baseContext)
//----- Adapters: -----
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playersNicknames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choosePlayer.adapter = adapter
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playersRate)
        listView.adapter = adapter
//----- Data: -----
        //dbHandler.createDB()
        _getPlayers()
    }

    override fun onResume() {
        super.onResume()
        //----- Music: -----
        mediaPlayer = MediaPlayer.create(this, R.raw.main_menu)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    override fun onPause() {
        mediaPlayer.stop()
        super.onPause()
    }

    private fun _getPlayers(){
        this.players.clear()
        try {
            players.addAll(dbHandler.getFromDB())
        }
        catch (e : Exception){
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
        this.players.sort()
        this.playersNicknames.clear()
        this.playersNicknames.add(defaultNickname)
        this.players.forEach { playersNicknames.add(it.Name)}
        this.playersRate.clear()
        this.players.forEach { playersRate.add(it.toString()) }

        var adapter = listView.adapter as ArrayAdapter<*>
        adapter.notifyDataSetChanged()
        adapter = choosePlayer.adapter as ArrayAdapter<*>
        adapter.notifyDataSetChanged()
        return
    }

    private fun _addPlayer(name: String){
        val newPlayer = Player(name, 0f, 0)
        dbHandler.addToDB(newPlayer)
        _getPlayers()
        choosePlayer.setSelection(playersNicknames.indexOf(newPlayer.Name))
    }

    private  fun _deletePlayer(index: Int){
        var name : String = playersRate[index]
        name = name.substring(0, name.indexOf('_') - 1)
        var player : Player? = null
        for(it in players) { if(name == it.Name) {player = it; break; }}
        dbHandler.rmFromDB(player!!)
        _getPlayers()
        choosePlayer.setSelection(0)
    }

    private fun _updatePlayer(player: Player){
        dbHandler.updInDB(player)
        _getPlayers()
        choosePlayer.setSelection(playersNicknames.indexOf(player.Name))
    }

    fun registerClick(view : View){
        registerIsClicked = !registerIsClicked
        if(registerIsClicked){
            enterName.isVisible = true
            choosePlayer.isVisible = false
            buttonRegister.text = resources.getString(R.string.accept)
        }
        else{
            val name : String = enterName.text.toString()
            if(name.isNotEmpty() && name !in playersNicknames){
                _addPlayer(name)
            }
            else{
                Toast.makeText(applicationContext, resources.getString(R.string.name_error), Toast.LENGTH_SHORT).show()
            }
            enterName.isVisible = false
            choosePlayer.isVisible = true
            buttonRegister.text = resources.getString(R.string.register)
            enterName.text.clear()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    fun rateClick(view: View){
        rateIsClicked = !rateIsClicked
        if(rateIsClicked){
            imageView.isVisible = false
            listView.isVisible = true
            buttonRate.text = resources.getString(R.string.back)
        }
        else{
            imageView.isVisible = true
            listView.isVisible = false
            buttonRate.text = resources.getString(R.string.rate)
        }
    }

    fun startClick(view: View){
        if(choosePlayer.selectedItem as String == defaultNickname){
            Toast.makeText(applicationContext, defaultNickname, Toast.LENGTH_SHORT).show()
            return
        }
        else{
            val selectedName = choosePlayer.selectedItem as String
            this.players.forEach { if(it.Name == selectedName) this.currentPlayer = it }
        }

        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("player", currentPlayer)
        startActivityForResult(intent, reqestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == this.reqestCode){
            this.currentPlayer = data?.extras?.get("player") as Player
            _updatePlayer(currentPlayer)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.getMenuInfo() as AdapterView.AdapterContextMenuInfo
        return when (item.getItemId()) {
            R.id.delete -> {
                _deletePlayer(info.position)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}