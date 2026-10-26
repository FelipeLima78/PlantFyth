package com.network.plantfyth;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.network.plantfyth.model.Especime;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaCadastroEspecime extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";

    private static final String SELECIONE = "Selecione";

    private static final String UN_HORAS = "Horas";
    private static final String UN_DIAS = "Dias";
    private static final String UN_SEMANAS = "Semanas";
    private static final String UN_MESES = "Meses";
    private static final String UN_ANOS = "Anos";
    private static final String UN_NAO_PRECISA = "Não precisa";

    private static final int HORAS_MAX = 8760;
    private static final Pattern PADRAO_PERIODO = Pattern.compile(
            "^(?:a cada\\s+)?(\\d+)\\s*(dias?|semanas?|meses|mes|anos?)$");

    private EditText edtNomePopular, edtNomeCientifico, edtDescricao;
    private EditText edtFamilia, edtCiclo, edtTamanhoAdulto;
    private EditText edtPeriodoIrrigacao, edtAdubacao, edtPeriodoPoda;
    private EditText edtTrocaVaso, edtTemperatura;

    private Spinner spnCrescimento, spnVolumeIrrigacao, spnLuz;
    private Spinner spnUnidadeIrrigacao, spnUnidadeAdubacao;
    private Spinner spnUnidadePoda, spnUnidadeVaso;

    private Button btnSalvar, btnCancelar;
    private TextView txtTitulo;
    private ProgressBar progressForm;

    private final PlantFythAPI api =
            new RetroFitService().getRetrofit().create(PlantFythAPI.class);

    private int especieId = -1;

    private Especime especimeAtual;
    private String nomeCientificoOriginal;
    private final String[] unidadesAdubacao = {
            UN_DIAS, UN_SEMANAS, UN_MESES, UN_ANOS, UN_NAO_PRECISA
    };

    private final String[] unidadesPoda = {
            UN_MESES, UN_ANOS, UN_NAO_PRECISA
    };

    private final String[] unidadesTrocaVaso = {
            UN_MESES, UN_ANOS, UN_NAO_PRECISA
    };
    private final String[] crescimentos = {"Devagar", "Médio", "Rápido"};
    private final String[] volumes = {"Baixa", "Moderada", "Alta"};
    private final String[] luzes = {"Sol pleno", "Luz filtrada", "Sombra Parcial", "Sombra Profunda"};

    private final String[] unidadesIrrigacao = {UN_DIAS, UN_SEMANAS, UN_HORAS};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro_especime);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomInset = Math.max(systemBars.bottom, ime.bottom);
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset);
            return insets;
        });

        inicializarViews();
        configurarSpinners();

        findViewById(R.id.btnVoltarEspecies).setOnClickListener(v -> finish());

        especieId = getIntent().getIntExtra(EXTRA_ID, -1);

        if (especieId != -1) {
            txtTitulo.setText("Editar espécie");
            btnSalvar.setText("Salvar alterações");
            buscarEspecime();
        } else {
            txtTitulo.setText("Nova espécie");
        }

        btnSalvar.setOnClickListener(v -> salvarEspecime());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void inicializarViews() {
        txtTitulo = findViewById(R.id.txtTituloForm);
        progressForm = findViewById(R.id.progressForm);

        edtNomePopular = findViewById(R.id.edtNomePopular);
        edtNomeCientifico = findViewById(R.id.edtNomeCientifico);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtFamilia = findViewById(R.id.edtFamilia);
        edtCiclo = findViewById(R.id.edtCiclo);
        edtTamanhoAdulto = findViewById(R.id.edtTamanhoAdulto);
        edtPeriodoIrrigacao = findViewById(R.id.edtPeriodoIrrigacao);
        edtAdubacao = findViewById(R.id.edtAdubacao);
        edtPeriodoPoda = findViewById(R.id.edtPeriodoPoda);
        edtTrocaVaso = findViewById(R.id.edtTrocaVaso);
        edtTemperatura = findViewById(R.id.edtTemperatura);

        spnCrescimento = findViewById(R.id.spnCrescimento);
        spnVolumeIrrigacao = findViewById(R.id.spnVolumeIrrigacao);
        spnLuz = findViewById(R.id.spnLuz);
        spnUnidadeIrrigacao = findViewById(R.id.spnUnidadeIrrigacao);
        spnUnidadeAdubacao = findViewById(R.id.spnUnidadeAdubacao);
        spnUnidadePoda = findViewById(R.id.spnUnidadePoda);
        spnUnidadeVaso = findViewById(R.id.spnUnidadeVaso);

        btnSalvar = findViewById(R.id.btnSalvarEspecime);
        btnCancelar = findViewById(R.id.btnCancelarEspecime);
    }

    // --------------------------------------------------------------- spinners

    private void configurarSpinners() {
        configurarSpinner(spnCrescimento, comSelecione(crescimentos));
        configurarSpinner(spnVolumeIrrigacao, comSelecione(volumes));
        configurarSpinner(spnLuz, comSelecione(luzes));

        configurarSpinner(spnUnidadeIrrigacao, unidadesIrrigacao);
        configurarSpinner(spnUnidadeAdubacao, unidadesAdubacao);
        configurarSpinner(spnUnidadePoda, unidadesPoda);
        configurarSpinner(spnUnidadeVaso, unidadesTrocaVaso);

        ligarCampoPeriodo(edtAdubacao, spnUnidadeAdubacao);
        ligarCampoPeriodo(edtPeriodoPoda, spnUnidadePoda);
        ligarCampoPeriodo(edtTrocaVaso, spnUnidadeVaso);
    }

    private String[] comSelecione(String[] opcoes) {
        String[] r = new String[opcoes.length + 1];
        r[0] = SELECIONE;
        System.arraycopy(opcoes, 0, r, 1, opcoes.length);
        return r;
    }

    private void configurarSpinner(Spinner spinner, String[] opcoes) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }


    private void ligarCampoPeriodo(EditText campo, Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean especial = ehEspecial(String.valueOf(parent.getItemAtPosition(position)));
                campo.setEnabled(!especial);
                if (especial) campo.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private boolean ehEspecial(String unidade) {
        return  UN_NAO_PRECISA.equals(unidade);
    }

    /** Seleciona o valor; se ele não estiver na lista, entra na lista em vez de ser trocado. */
    private void selecionarSpinner(Spinner spinner, String valor) {
        if (spinner == null || valor == null || valor.trim().isEmpty()) {
            return;
        }

        android.widget.SpinnerAdapter adapter = spinner.getAdapter();

        if (adapter == null) {
            return;
        }

        String valorProcurado = valor.trim();

        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);

            if (item != null &&
                    item.toString().equalsIgnoreCase(valorProcurado)) {
                spinner.setSelection(i);
                return;
            }
        }

        // O valor salvo não existe nas opções atuais.
        // Não altera a lista do spinner automaticamente.
        android.util.Log.w(
                "TelaCadastroEspecime",
                "Valor não encontrado no spinner: " + valorProcurado
        );
    }


    private void buscarEspecime() {
        mostrarCarregamento(true);

        api.buscarEspecimePorId(especieId).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(@NonNull Call<Especime> call, @NonNull Response<Especime> response) {
                mostrarCarregamento(false);

                if (response.isSuccessful() && response.body() != null) {
                    especimeAtual = response.body();
                    preencherFormulario(especimeAtual);
                } else {
                    Toast.makeText(TelaCadastroEspecime.this,
                            "Erro ao carregar espécie: HTTP " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Especime> call, @NonNull Throwable t) {
                mostrarCarregamento(false);
                Toast.makeText(TelaCadastroEspecime.this,
                        "Falha na conexão: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void preencherFormulario(Especime e) {
        nomeCientificoOriginal = e.getNome_cientifico();

        edtNomePopular.setText(valorTexto(e.getNome_popular()));
        edtNomeCientifico.setText(valorTexto(e.getNome_cientifico()));
        edtDescricao.setText(valorTexto(e.getDescricao()));
        edtFamilia.setText(valorTexto(e.getFamilia()));
        edtCiclo.setText(valorTexto(e.getCiclo()));
        edtTemperatura.setText(valorTexto(e.getTemperatura()));

        if (e.getTamanho_adulto_cm() != null) {
            edtTamanhoAdulto.setText(String.valueOf(e.getTamanho_adulto_cm()));
        }

        // O banco guarda HORAS. Mostra na maior unidade que divide certinho.
        if (e.getPeriodo_irrigacao() != null) {
            int horas = e.getPeriodo_irrigacao();

            if (horas >= 168 && horas % 168 == 0) {
                edtPeriodoIrrigacao.setText(String.valueOf(horas / 168));
                selecionarSpinner(spnUnidadeIrrigacao, UN_SEMANAS);
            } else if (horas >= 24 && horas % 24 == 0) {
                edtPeriodoIrrigacao.setText(String.valueOf(horas / 24));
                selecionarSpinner(spnUnidadeIrrigacao, UN_DIAS);
            } else {
                edtPeriodoIrrigacao.setText(String.valueOf(horas));
                selecionarSpinner(spnUnidadeIrrigacao, UN_HORAS);
            }
        }

        selecionarSpinner(spnCrescimento, e.getCrescimento());
        selecionarSpinner(spnVolumeIrrigacao, e.getVolume_irrigacao());
        selecionarSpinner(spnLuz, e.getExposicao_a_luz());

        edtAdubacao.setText(valorTexto(e.getAdubacao()));
        edtPeriodoPoda.setText(valorTexto(e.getPeriodo_poda()));
        edtTrocaVaso.setText(valorTexto(e.getTroca_de_vaso()));
    }

    private String valorTexto(String valor) {
        return valor == null ? "" : valor;
    }




    private String unidadePorTexto(String u) {
        if (u.startsWith("dia")) return UN_DIAS;
        if (u.startsWith("sem")) return UN_SEMANAS;
        if (u.startsWith("mes")) return UN_MESES;
        return UN_ANOS;
    }


    private String[] periodoPorPalavra(String t) {
        switch (t) {
            case "diaria":
            case "diariamente":
                return new String[]{"1", UN_DIAS};
            case "semanal":
                return new String[]{"1", UN_SEMANAS};
            case "quinzenal":
                return new String[]{"15", UN_DIAS};
            case "mensal":
                return new String[]{"1", UN_MESES};
            case "bimestral":
                return new String[]{"2", UN_MESES};
            case "trimestral":
                return new String[]{"3", UN_MESES};
            case "semestral":
                return new String[]{"6", UN_MESES};
            case "anual":
                return new String[]{"1", UN_ANOS};
            default:
                return null;
        }
    }

    private String montarPeriodo(
            EditText campo,
            Spinner spinner,
            String textoNaoPrecisa
    ) {
        String texto = campo.getText().toString().trim();

        if (texto.isEmpty()) {
            return null;
        }

        String normalizado = CuidadoCalculator.normalizar(texto).trim();

        // Mantém os intervalos já padronizados.
        switch (normalizado) {
            case "diaria":
            case "diariamente":
                return "Diária";
            case "semanal":
                return "Semanal";
            case "quinzenal":
                return "Quinzenal";
            case "mensal":
                return "Mensal";
            case "bimestral":
                return "Bimestral";
            case "trimestral":
                return "Trimestral";
            case "semestral":
                return "Semestral";
            case "anual":
                return "Anual";
        }

        // Se for uma frase, preserva o texto inteiro.
        if (texto.matches(".*[a-zA-ZÀ-ÿ].*")) {
            return texto;
        }

        // Se for número, precisa de uma unidade válida.
        String unidade = spinner.getSelectedItem() == null
                ? null
                : spinner.getSelectedItem().toString();

        if (unidade == null) {
            campo.setError("Selecione a unidade do período");
            return null;
        }

        if (UN_NAO_PRECISA.equals(unidade)) {
            return textoNaoPrecisa;
        }

        try {
            int n = Integer.parseInt(texto);

            if (n <= 0) {
                campo.setError("O período deve ser maior que zero");
                return null;
            }

            return textoPadrao(n, unidade);

        } catch (NumberFormatException ex) {
            campo.setError("Informe um número válido ou uma instrução");
            return null;
        }
    }

    /** Nome quando existe (Semanal, Mensal...); senão dias, meses ou anos. */
    private String textoPadrao(int n, String unidade) {
        switch (unidade) {
            case UN_DIAS:
                if (n == 1) return "Diária";
                if (n == 7) return "Semanal";
                if (n == 15) return "Quinzenal";
                if (n == 30) return "Mensal";
                if (n == 60) return "Bimestral";
                if (n == 90) return "Trimestral";
                if (n == 180) return "Semestral";
                if (n == 365) return "Anual";
                return "A cada " + n + " dias";
            case UN_SEMANAS:
                if (n == 1) return "Semanal";
                return textoPadrao(n * 7, UN_DIAS); // semanas viram dias
            case UN_MESES:
                if (n == 1) return "Mensal";
                if (n == 2) return "Bimestral";
                if (n == 3) return "Trimestral";
                if (n == 6) return "Semestral";
                if (n == 12) return "Anual";
                return "A cada " + n + " meses";
            case UN_ANOS:
                if (n == 1) return "Anual";
                return "A cada " + n + " anos";
            default:
                return null;
        }
    }

    // ----------------------------------------------------------------- salvar

    private void salvarEspecime() {
        String nomePopular = edtNomePopular.getText().toString().trim();
        String nomeCientifico = edtNomeCientifico.getText().toString().trim();
        String intervaloTexto = edtPeriodoIrrigacao.getText().toString().trim();

        if (nomePopular.isEmpty()) {
            edtNomePopular.setError("Informe o nome popular");
            edtNomePopular.requestFocus();
            return;
        }

        if (nomeCientifico.isEmpty()) {
            edtNomeCientifico.setError("Informe o nome científico");
            edtNomeCientifico.requestFocus();
            return;
        }

        if (intervaloTexto.isEmpty()) {
            edtPeriodoIrrigacao.setError("Informe o intervalo de irrigação");
            edtPeriodoIrrigacao.requestFocus();
            return;
        }

        int intervaloInformado;
        try {
            intervaloInformado = Integer.parseInt(intervaloTexto);
        } catch (NumberFormatException ex) {
            edtPeriodoIrrigacao.setError("Informe um número válido");
            edtPeriodoIrrigacao.requestFocus();
            return;
        }

        if (intervaloInformado <= 0) {
            edtPeriodoIrrigacao.setError("O intervalo deve ser maior que zero");
            edtPeriodoIrrigacao.requestFocus();
            return;
        }

        // Sempre salva em HORAS, qualquer que seja a unidade escolhida.
        String unidadeRega = spnUnidadeIrrigacao.getSelectedItem().toString();
        long fator = UN_SEMANAS.equals(unidadeRega) ? 168 : UN_DIAS.equals(unidadeRega) ? 24 : 1;
        long horas = (long) intervaloInformado * fator;

        if (horas > HORAS_MAX) {
            edtPeriodoIrrigacao.setError("Máximo de 1 ano (" + HORAS_MAX + " horas)");
            edtPeriodoIrrigacao.requestFocus();
            return;
        }

        // Obrigatórios, validados na ordem em que aparecem na tela.
        // Volume de água e luz sustentam o glossário e a orientação de rega.
        if (spnVolumeIrrigacao.getSelectedItemPosition() <= 0) {
            erroSpinner(spnVolumeIrrigacao, "Selecione o volume de irrigação");
            return;
        }

        if (spnLuz.getSelectedItemPosition() <= 0) {
            erroSpinner(spnLuz, "Selecione a exposição à luz");
            return;
        }

        String tamanhoTexto = edtTamanhoAdulto.getText().toString().trim();
        Float tamanho = null;
        if (!tamanhoTexto.isEmpty()) {
            try {
                tamanho = Float.parseFloat(tamanhoTexto.replace(",", "."));
            } catch (NumberFormatException ex) {
                edtTamanhoAdulto.setError("Informe um tamanho válido");
                edtTamanhoAdulto.requestFocus();
                return;
            }
        }

        // edição: altera a espécie carregada; cadastro: cria uma nova
        Especime e = especimeAtual != null ? especimeAtual : new Especime();

        e.setNome_popular(nomePopular);
        e.setNome_cientifico(nomeCientifico);
        e.setDescricao(textoOuNull(edtDescricao));
        e.setFamilia(textoOuNull(edtFamilia));
        e.setCiclo(textoOuNull(edtCiclo));
        e.setCrescimento(valorSpinnerOpcional(spnCrescimento));
        e.setExposicao_a_luz(valorSpinnerOpcional(spnLuz));
        e.setVolume_irrigacao(valorSpinnerOpcional(spnVolumeIrrigacao));
        e.setPeriodo_irrigacao((int) horas);

        e.setAdubacao(
                montarPeriodo(edtAdubacao, spnUnidadeAdubacao, null)
        );

        e.setPeriodo_poda(
                montarPeriodo(edtPeriodoPoda, spnUnidadePoda, null)
        );

        e.setTroca_de_vaso(
                montarPeriodo(edtTrocaVaso, spnUnidadeVaso, null)
        );
        e.setTemperatura(textoOuNull(edtTemperatura));
        e.setTamanho_adulto_cm(tamanho);

        mostrarCarregamento(true);

        // Só consulta duplicidade quando é espécie nova ou o nome científico mudou.
        String novo = norm(nomeCientifico);
        boolean checarDuplicidade = especieId == -1 || !novo.equals(norm(nomeCientificoOriginal));

        if (checarDuplicidade) {
            verificarDuplicidadeEEnviar(e, novo);
        } else {
            enviar(e);
        }
    }

    private static String norm(String texto) {
        return texto == null ? "" : CuidadoCalculator.normalizar(texto).trim();
    }

    private String textoOuNull(EditText campo) {
        String texto = campo.getText().toString().trim();
        return texto.isEmpty() ? null : texto;
    }

    private void erroSpinner(Spinner spinner, String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
        spinner.requestRectangleOnScreen(
                new Rect(0, 0, spinner.getWidth(), spinner.getHeight()), false);
        View v = spinner.getSelectedView();
        if (v instanceof TextView) ((TextView) v).setError(mensagem);
    }

    private void verificarDuplicidadeEEnviar(Especime e, String novo) {
        api.listarEspecimes().enqueue(new Callback<List<Especime>>() {
            @Override
            public void onResponse(@NonNull Call<List<Especime>> call,
                                   @NonNull Response<List<Especime>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    mostrarCarregamento(false);
                    Toast.makeText(TelaCadastroEspecime.this,
                            "Não foi possível verificar duplicidade. Tente novamente.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                for (Especime outra : response.body()) {
                    boolean mesmoNome = norm(outra.getNome_cientifico()).equals(novo);
                    boolean outroRegistro = especieId == -1 || outra.getId() != especieId;

                    if (mesmoNome && outroRegistro) {
                        mostrarCarregamento(false);
                        new AlertDialog.Builder(TelaCadastroEspecime.this)
                                .setTitle("Espécie já cadastrada")
                                .setMessage("Já existe uma espécie com o nome científico \""
                                        + outra.getNome_cientifico()
                                        + "\". Edite a existente em vez de criar outra.")
                                .setPositiveButton("Entendi", null)
                                .show();
                        return;
                    }
                }
                enviar(e);
            }

            @Override
            public void onFailure(@NonNull Call<List<Especime>> call, @NonNull Throwable t) {
                mostrarCarregamento(false);
                Toast.makeText(TelaCadastroEspecime.this,
                        "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enviar(Especime e) {
        if (especieId == -1) {
            criarEspecime(e);
        } else {
            atualizarEspecime(e);
        }
    }

    private void criarEspecime(Especime e) {
        api.criarEspecime(e).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(@NonNull Call<Especime> call, @NonNull Response<Especime> response) {
                mostrarCarregamento(false);

                if (response.isSuccessful()) {
                    Toast.makeText(TelaCadastroEspecime.this,
                            "Espécie cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TelaCadastroEspecime.this,
                            "Erro ao cadastrar: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Especime> call, @NonNull Throwable t) {
                mostrarCarregamento(false);
                Toast.makeText(TelaCadastroEspecime.this,
                        "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atualizarEspecime(Especime e) {
        api.atualizarEspecime(especieId, e).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(@NonNull Call<Especime> call, @NonNull Response<Especime> response) {
                mostrarCarregamento(false);

                if (response.isSuccessful()) {
                    Toast.makeText(TelaCadastroEspecime.this,
                            "Espécie atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TelaCadastroEspecime.this,
                            "Erro ao atualizar: HTTP " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Especime> call, @NonNull Throwable t) {
                mostrarCarregamento(false);
                Toast.makeText(TelaCadastroEspecime.this,
                        "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarCarregamento(boolean carregando) {
        if (progressForm != null) {
            progressForm.setVisibility(carregando ? View.VISIBLE : View.GONE);
        }
        if (btnSalvar != null) {
            btnSalvar.setEnabled(!carregando);
        }
    }
    private String valorSpinnerOpcional(Spinner spinner) {
        if (spinner.getSelectedItemPosition() <= 0) {
            return null;
        }

        Object valor = spinner.getSelectedItem();
        return valor == null ? null : valor.toString();
    }
}
