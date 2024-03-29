package com.example.maisbarato.presentation.view

import android.content.ContentResolver
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maisbarato.R
import com.example.maisbarato.databinding.FragmentCrudOfertaBinding
import com.example.maisbarato.data.model.Oferta
import com.example.maisbarato.util.DateUtil
import com.example.maisbarato.util.StateViewResult
import com.example.maisbarato.view.adapter.ListaImagemAdapter
import com.example.maisbarato.viewmodel.CrudOfertaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@AndroidEntryPoint
class CrudOfertaFragment : Fragment() {

    private var _binding: FragmentCrudOfertaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CrudOfertaViewModel by viewModels()
    private lateinit var launcher: ActivityResultLauncher<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var listaImagemAdapter: ListaImagemAdapter

    private lateinit var contentResolver: ContentResolver

    private var listaImagens = listOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCrudOfertaBinding.inflate(inflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configuracaoView()
        configuracaoSelecaoImagem()
        listeners()
        observers()
    }

    private fun observers() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ofertaStateView.collect { stateViewResult ->

                    when (stateViewResult) {
                        is StateViewResult.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is StateViewResult.Success -> {
                            binding.progressBar.visibility = View.GONE
                            findNavController().navigate(R.id.listaOfertasFragment)
                        }
                        is StateViewResult.Error -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }

                }
            }
        }
    }

    private fun configuracaoSelecaoImagem() {
        launcher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri ->
            uri?.also { listUri ->

                listaImagemAdapter.atualizaListaImagens(listUri.map { it.toString() })

                listaImagens = listUri.mapNotNull { it.toString() }


                if (listUri.isNotEmpty()) {
                    binding.imagemOferta.visibility = View.INVISIBLE
                    binding.botaoTrocarImagens.visibility = View.VISIBLE
                    binding.tvErroImagem.visibility = View.INVISIBLE
                } else {
                    binding.imagemOferta.visibility = View.VISIBLE
                    binding.botaoTrocarImagens.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun configuracaoView() {
        recyclerView = binding.recyclerViewImagens
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        listaImagemAdapter = ListaImagemAdapter(listOf())
        recyclerView.adapter = listaImagemAdapter

        contentResolver = requireContext().contentResolver
    }

    private fun listeners() {
        binding.btnAddImagem.setOnClickListener {
            abrirSelecaoImagem()
        }

        binding.btnSalvar.setOnClickListener {

            if (validaCampos()) {
                salvaOferta()
            } else {
                mostraCamposComErro()
            }
        }

        binding.botaoTrocarImagens.setOnClickListener {
            abrirSelecaoImagem()
        }

        binding.editTextPrecoAntigo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            private var precoAntigo = ""
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != precoAntigo) {
                    precoAntigo = formataTextoPreco(s)
                    binding.editTextPrecoAntigo.setText(precoAntigo)
                    binding.editTextPrecoAntigo.setSelection(precoAntigo.length)
                }
            }
        })

        binding.editTextPrecoNovo.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            private var precoNovo = ""
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != precoNovo) {
                    precoNovo = formataTextoPreco(s)
                    binding.editTextPrecoNovo.setText(precoNovo)
                    binding.editTextPrecoNovo.setSelection(precoNovo.length)
                    binding.crudPrecoNovo.error = ""
                }
            }
        })

        binding.editTextTitulo.doAfterTextChanged {
            it?.toString()?.also { texto ->
                binding.crudTitulo.error = if(texto.length < 10 ) {
                    getString(R.string.min_10_caracteres)
                } else {
                    null
                }
            }
        }

        binding.editTextEstabelecimento.doAfterTextChanged {
            it?.toString()?.also { texto ->
                binding.crudEstabelecimento.error = if (texto.length < 3) {
                    getString(R.string.min_3_caracteres)
                } else {
                    null
                }
            }
        }

        binding.editTextEnderecoEstabelecimento.doAfterTextChanged {
            it?.toString()?.also { texto ->
                binding.crudEnderecoEstabelecimento.error = if (texto.length < 10) {
                    getString(R.string.min_10_caracteres)
                } else {
                    null
                }
            }
        }

        binding.editTextInfoAdicionais.doAfterTextChanged {

            it?.toString()?.also { texto ->
                binding.crudInfoAdicionais.error = if(texto.length < 10) {
                    getString(R.string.min_10_caracteres)
                } else {
                    null
                }
            }
        }
    }

    private fun mostraCamposComErro() {
        if (listaImagens.isEmpty()) {
            binding.tvErroImagem.visibility = View.VISIBLE
        }

        if (binding.editTextTitulo.text.isNullOrEmpty()) {
            binding.crudTitulo.error = getString(R.string.digite_titulo)
        }

        if (binding.editTextEstabelecimento.text.isNullOrEmpty()) {
            binding.crudEstabelecimento.error = getString(R.string.selecione_estabelecimento)
        }

        if (binding.editTextEnderecoEstabelecimento.text.isNullOrEmpty()) {
            binding.crudEnderecoEstabelecimento.error = getString(R.string.endereco_estabelecimento)
        }

        if (binding.editTextPrecoNovo.text.isNullOrEmpty()) {
            binding.crudPrecoNovo.error = getString(R.string.digite_preco_atual)
        }

        if (binding.editTextInfoAdicionais.text.isNullOrEmpty()) {
            binding.crudInfoAdicionais.error = getString(R.string.digite_detalhes)
        }
    }

    private fun abrirSelecaoImagem() {
        launcher.launch("image/*")
    }

    private fun validaCampos(): Boolean {

        return listaImagens.isNotEmpty() &&
                binding.editTextTitulo.text?.isNotEmpty() == true &&
                binding.editTextTitulo.length() > 3 &&
                binding.editTextEstabelecimento.text?.isNotEmpty() == true &&
                binding.editTextEnderecoEstabelecimento.text?.isNotEmpty() == true &&
                binding.editTextPrecoNovo.text?.isNotEmpty() == true &&
                binding.editTextInfoAdicionais.text?.isNotEmpty() == true &&
                binding.editTextInfoAdicionais.length() > 3
    }

    private fun formataTextoPreco(s: Editable?): String {

        val cleanString: String = s?.toString()?.replace("""[R$,.]""".toRegex(), "")
            ?.replace("\\s".toRegex(), "") ?: ""

        val parsed = cleanString.toDouble()
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format((parsed / 100))

    }

    private fun salvaOferta() {

        val oferta = Oferta(
            listaUrlImagem = listaImagens,
            titulo = binding.editTextTitulo.text.toString(),
            nomeLoja = binding.editTextEstabelecimento.text.toString(),
            endereco = binding.editTextEnderecoEstabelecimento.text.toString(),
            valorAntigo = formatarPrecoParaSalvar(binding.editTextPrecoAntigo.text?.toString()),
            valorNovo = formatarPrecoParaSalvar(binding.editTextPrecoNovo.text?.toString()),
            dataInclusao = DateUtil.getCurrentTime(),
            descricao = binding.editTextInfoAdicionais.text.toString(),
            userUid = viewModel.userUid
        )

        viewModel.adicionaOferta(oferta)
    }

    private fun formatarPrecoParaSalvar(preco: String?): Double? {

        return preco?.replace("\\s".toRegex(),"")
            ?.replace("R$","")
            ?.replace(",", ".")
            ?.toDoubleOrNull()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}