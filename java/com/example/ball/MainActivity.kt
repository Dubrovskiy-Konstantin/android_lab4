package com.example.ball

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible


class MainActivity : AppCompatActivity() {
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
    private val dbHandler = DBHandler()

    private class DBHandler(){
        fun getFromDB(){}
        fun addToDB(newPlayer: Player){}
        fun rmFromDB(rmPlayer: Player){}
        fun updInDB(updPlayer: Player){}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        }
        catch (e: NullPointerException) {
        }
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

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
        defaultNickname = resources.getString(R.string.choose_player)

        _getPlayers()

        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, playersNicknames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choosePlayer.adapter = adapter

        players.forEach { playersRate.add(it.toString()) }
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playersRate)
        listView.adapter = adapter

        mediaPlayer = MediaPlayer.create(this, R.raw.main_menu)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    private fun _getPlayers(){
        val players = ArrayList<Player>()
        try {
            dbHandler.getFromDB()
        }
        catch (e : Exception){
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
        //-----------------------------------
        players.add(Player("Nick", 3700.5f, 100))
        players.add(Player("John", 232.1f, 5))
        players.add(Player("Billy", 420.9f, 3))
        players.add(Player("Ricardo", 419.7f, 7))
        //-----------------------------------
        players.sort()
        this.players.clear()
        this.players.addAll(players)
        this.playersNicknames.clear()
        this.playersNicknames.add(defaultNickname)
        this.players.forEach {
            this.playersNicknames.add(it.Name)
        }

        players.clear()
        return
    }

    private fun _addPlayer(name: String){
        val newPlayer = Player(name, 0f, 0)
        dbHandler.addToDB(newPlayer)
        players.add(newPlayer)
        playersNicknames.add(newPlayer.Name)
        playersRate.add(newPlayer.toString())
        var adapter = listView.adapter as ArrayAdapter<*>
        adapter.notifyDataSetChanged()
        adapter = choosePlayer.adapter as ArrayAdapter<*>
        adapter.notifyDataSetChanged()
    }

    private  fun _deletePlayer(index: Int){
        var name : String = playersRate[index]
        name = name.substring(0, name.indexOf('_') - 1)
        var player : Player? = null
        for(it in players) { if(name == it.Name) {player = it; break; }}
        players.remove(player!!)
        playersRate.remove(player.toString())
        playersNicknames.remove(player.Name)
        var adapter = listView.adapter as ArrayAdapter<*>
        adapter.notifyDataSetChanged()
        adapter = choosePlayer.adapter as ArrayAdapter<*>
        adapter.notifyDataSetChanged()
        dbHandler.rmFromDB(player)
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

        Toast.makeText(applicationContext, currentPlayer.Name, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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