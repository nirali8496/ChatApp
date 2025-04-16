package com.example.chatapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.ui.adapter.ChatAdapter
import com.example.chatapp.ui.adapter.ChatMessageAdapter
import com.example.chatapp.ui.viewmodel.ChatViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var adapter: ChatMessageAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        adapter = ChatMessageAdapter(mutableListOf())
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = adapter

        viewModel.messages.observe(this) {
            adapter.updateMessages(it)
            binding.chatRecyclerView.scrollToPosition(it.size - 1)
        }


        viewModel.connectSocket()

        viewModel.messages.observe(this) { msgs ->
            adapter.notifyDataSetChanged()
            binding.chatRecyclerView.scrollToPosition(msgs.size - 1)
        }

        binding.sendButton.setOnClickListener {
            val msg = binding.messageEditText.text.toString().trim()
            if (msg.isNotEmpty()) {
                viewModel.sendMessage(msg)
                binding.messageEditText.text.clear()
            }
        }



        /*  ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
              val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
              v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
              insets
          }*/
    }
}