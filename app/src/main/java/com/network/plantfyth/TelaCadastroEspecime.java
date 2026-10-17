
package com.network.plantfyth;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.network.plantfyth.model.Especime;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaCadastroEspecime extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";

    private EditText edtNomePopular, edtNomeCientifico, edtDescricao;
    private EditText edtFamilia, edtCiclo, edtTamanhoAdulto;
    private EditText edtPeriodoIrrigacao, edtAdubacao, edtPeriodoPoda;
    private EditText edtDetalhesPoda, edtTrocaVaso, edtTemperatura;

    private Spinner spnCrescimento, spnVolumeIrrigacao, spnLuz;
    private Spinner spnUnidadeIrrigacao, spnUnidadeAdubacao;
    private Spinner spnUnidadePoda, spnUnidadeVaso;

    private CheckBox chkPodaComplicada;

    private Button btnSalvar, btnCancelar;
    private TextView txtTitulo;
    private ProgressBar progressForm;

    private final PlantFythAPI api =
            new RetroFitService().getRetrofit().create(PlantFythAPI.class);

    private int especieId = -1;

    private final String[] crescimentos = {
            "Devagar", "Médio", "Rápido"
    };

    private final String[] volumes = {
            "Baixa", "Moderada", "Alta"
    };

    private final String[] luzes = {
            "Sol pleno", "Luz filtrada",
            "Sombra Parcial", "Sombra Profunda"
    };

    private final String[] unidadesIrrigacao = {
            "Dias", "Semanas"
    };

    private final String[] unidadesTempo = {
            "Dias", "Semanas", "Meses", "Anos", "Semestral"
    };

    private final String[] unidadesPoda = {
            "Anos", "Meses", "Semestral"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_especime);

        inicializarViews();
        configurarSpinners();

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
        edtDetalhesPoda = findViewById(R.id.edtDetalhesPoda);
        edtTrocaVaso = findViewById(R.id.edtTrocaVaso);
        edtTemperatura = findViewById(R.id.edtTemperatura);

        spnCrescimento = findViewById(R.id.spnCrescimento);
        spnVolumeIrrigacao = findViewById(R.id.spnVolumeIrrigacao);
        spnLuz = findViewById(R.id.spnLuz);
        spnUnidadeIrrigacao = findViewById(R.id.spnUnidadeIrrigacao);
        spnUnidadeAdubacao = findViewById(R.id.spnUnidadeAdubacao);
        spnUnidadePoda = findViewById(R.id.spnUnidadePoda);
        spnUnidadeVaso = findViewById(R.id.spnUnidadeVaso);

        chkPodaComplicada = findViewById(R.id.chkPodaComplicada);

        btnSalvar = findViewById(R.id.btnSalvarEspecime);
        btnCancelar = findViewById(R.id.btnCancelarEspecime);
    }

    private void configurarSpinners() {
        configurarSpinner(spnCrescimento, crescimentos);
        configurarSpinner(spnVolumeIrrigacao, volumes);
        configurarSpinner(spnLuz, luzes);
        configurarSpinner(spnUnidadeIrrigacao, unidadesIrrigacao);
        configurarSpinner(spnUnidadeAdubacao, unidadesTempo);
        configurarSpinner(spnUnidadePoda, unidadesPoda);
        configurarSpinner(spnUnidadeVaso, unidadesTempo);
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

    private void buscarEspecime() {
        mostrarCarregamento(true);

        api.buscarEspecimePorId(especieId).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(
                    @NonNull Call<Especime> call,
                    @NonNull Response<Especime> response
            ) {
                mostrarCarregamento(false);

                if (response.isSuccessful() && response.body() != null) {
                    preencherFormulario(response.body());
                } else {
                    Toast.makeText(
                            TelaCadastroEspecime.this,
                            "Erro ao carregar espécie: HTTP " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Especime> call,
                    @NonNull Throwable t
            ) {
                mostrarCarregamento(false);
                Toast.makeText(
                        TelaCadastroEspecime.this,
                        "Falha na conexão: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void preencherFormulario(Especime e) {
        edtNomePopular.setText(valorTexto(e.getNome_popular()));
        edtNomeCientifico.setText(valorTexto(e.getNome_cientifico()));
        edtDescricao.setText(valorTexto(e.getDescricao()));
        edtFamilia.setText(valorTexto(e.getFamilia()));
        edtCiclo.setText(valorTexto(e.getCiclo()));
        edtAdubacao.setText(valorTexto(e.getAdubacao()));
        edtPeriodoPoda.setText(valorTexto(e.getPeriodo_poda()));
        edtDetalhesPoda.setText("");
        edtTrocaVaso.setText(valorTexto(e.getTroca_de_vaso()));
        edtTemperatura.setText(valorTexto(e.getTemperatura()));

        if (e.getTamanho_adulto_cm() != null) {
            edtTamanhoAdulto.setText(
                    String.valueOf(e.getTamanho_adulto_cm())
            );
        }

        // O banco armazena o intervalo de irrigação em dias.
        // Se for divisível por 7 e tiver pelo menos 7 dias,
        // exibe o valor em semanas.
        if (e.getPeriodo_irrigacao() != null) {
            int dias = e.getPeriodo_irrigacao();

            if (dias >= 7 && dias % 7 == 0) {
                edtPeriodoIrrigacao.setText(String.valueOf(dias / 7));
                spnUnidadeIrrigacao.setSelection(1);
            } else {
                edtPeriodoIrrigacao.setText(String.valueOf(dias));
                spnUnidadeIrrigacao.setSelection(0);
            }
        }

        selecionarSpinner(spnCrescimento, e.getCrescimento());
        selecionarSpinner(spnVolumeIrrigacao, e.getVolume_irrigacao());
        selecionarSpinner(spnLuz, e.getExposicao_a_luz());

        preencherPeriodo(edtAdubacao, spnUnidadeAdubacao, e.getAdubacao());
        preencherPeriodo(edtPeriodoPoda, spnUnidadePoda, e.getPeriodo_poda());
        preencherPeriodo(edtTrocaVaso, spnUnidadeVaso, e.getTroca_de_vaso());
    }

    private String valorTexto(String valor) {
        return valor == null ? "" : valor;
    }

    private void selecionarSpinner(Spinner spinner, String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return;
        }

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString()
                    .equalsIgnoreCase(valor.trim())) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void preencherPeriodo(
            EditText campo,
            Spinner spinner,
            String periodo
    ) {
        if (periodo == null || periodo.trim().isEmpty()) {
            return;
        }

        Pattern pattern = Pattern.compile(
                "^\\s*(\\d+)\\s*(dias?|semanas?|meses?|anos?|semestral)?\\s*$",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(periodo.trim());

        if (!matcher.matches()) {
            campo.setText(periodo);
            return;
        }

        campo.setText(matcher.group(1));

        String unidade = matcher.group(2);

        if (unidade == null) {
            return;
        }

        String normalizada = unidade.toLowerCase(Locale.ROOT);

        if (normalizada.startsWith("dia")) {
            selecionarSpinner(spinner, "Dias");
        } else if (normalizada.startsWith("semana")) {
            selecionarSpinner(spinner, "Semanas");
        } else if (normalizada.startsWith("mes")) {
            selecionarSpinner(spinner, "Meses");
        } else if (normalizada.startsWith("ano")) {
            selecionarSpinner(spinner, "Anos");
        } else if (normalizada.startsWith("semestral")) {
            selecionarSpinner(spinner, "Semestral");
        }
    }

    private String montarPeriodo(EditText campo, Spinner spinner) {
        String numero = campo.getText().toString().trim();

        if (numero.isEmpty()) {
            return null;
        }

        return numero + " " +
                spinner.getSelectedItem().toString().toLowerCase(Locale.ROOT);
    }

    private void salvarEspecime() {
        String nomePopular = edtNomePopular.getText().toString().trim();
        String intervaloTexto = edtPeriodoIrrigacao.getText().toString().trim();

        if (nomePopular.isEmpty()) {
            edtNomePopular.setError("Informe o nome popular");
            edtNomePopular.requestFocus();
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

        long intervaloEmDias = intervaloInformado;

        if ("Semanas".equals(
                spnUnidadeIrrigacao.getSelectedItem().toString())) {
            intervaloEmDias *= 7;
        }

        if (intervaloEmDias > Integer.MAX_VALUE) {
            edtPeriodoIrrigacao.setError("Intervalo muito grande");
            edtPeriodoIrrigacao.requestFocus();
            return;
        }

        Especime e = new Especime();

        e.setNome_popular(nomePopular);
        e.setNome_cientifico(textoOuNull(edtNomeCientifico));
        e.setDescricao(textoOuNull(edtDescricao));
        e.setFamilia(textoOuNull(edtFamilia));
        e.setCiclo(textoOuNull(edtCiclo));
        e.setCrescimento(valorSpinner(spnCrescimento));
        e.setExposicao_a_luz(valorSpinner(spnLuz));
        e.setVolume_irrigacao(valorSpinner(spnVolumeIrrigacao));

        // Sempre salva em dias, independentemente da unidade selecionada.
        e.setPeriodo_irrigacao((int) intervaloEmDias);

        e.setAdubacao(montarPeriodo(edtAdubacao, spnUnidadeAdubacao));
        e.setPeriodo_poda(montarPeriodo(edtPeriodoPoda, spnUnidadePoda));
        e.setTroca_de_vaso(montarPeriodo(edtTrocaVaso, spnUnidadeVaso));
        e.setTemperatura(textoOuNull(edtTemperatura));

        String tamanhoTexto = edtTamanhoAdulto.getText().toString().trim();

        if (!tamanhoTexto.isEmpty()) {
            try {
                e.setTamanho_adulto_cm(Float.parseFloat(
                        tamanhoTexto.replace(",", ".")
                ));
            } catch (NumberFormatException ex) {
                edtTamanhoAdulto.setError("Informe um tamanho válido");
                edtTamanhoAdulto.requestFocus();
                return;
            }
        } else {
            e.setTamanho_adulto_cm(null);
        }

        mostrarCarregamento(true);

        if (especieId == -1) {
            criarEspecime(e);
        } else {
            atualizarEspecime(e);
        }
    }

    private String textoOuNull(EditText campo) {
        String texto = campo.getText().toString().trim();
        return texto.isEmpty() ? null : texto;
    }

    private String valorSpinner(Spinner spinner) {
        Object valor = spinner.getSelectedItem();
        return valor == null ? null : valor.toString();
    }

    private void criarEspecime(Especime e) {
        api.criarEspecime(e).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(
                    @NonNull Call<Especime> call,
                    @NonNull Response<Especime> response
            ) {
                mostrarCarregamento(false);

                if (response.isSuccessful()) {
                    Toast.makeText(
                            TelaCadastroEspecime.this,
                            "Espécie cadastrada com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                } else {
                    Toast.makeText(
                            TelaCadastroEspecime.this,
                            "Erro ao cadastrar: HTTP " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Especime> call,
                    @NonNull Throwable t
            ) {
                mostrarCarregamento(false);
                Toast.makeText(
                        TelaCadastroEspecime.this,
                        "Falha na conexão: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void atualizarEspecime(Especime e) {
        api.atualizarEspecime(especieId, e).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(
                    @NonNull Call<Especime> call,
                    @NonNull Response<Especime> response
            ) {
                mostrarCarregamento(false);

                if (response.isSuccessful()) {
                    Toast.makeText(
                            TelaCadastroEspecime.this,
                            "Espécie atualizada com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                } else {
                    Toast.makeText(
                            TelaCadastroEspecime.this,
                            "Erro ao atualizar: HTTP " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Especime> call,
                    @NonNull Throwable t
            ) {
                mostrarCarregamento(false);
                Toast.makeText(
                        TelaCadastroEspecime.this,
                        "Falha na conexão: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void mostrarCarregamento(boolean carregando) {
        if (progressForm != null) {
            progressForm.setVisibility(
                    carregando ? View.VISIBLE : View.GONE
            );
        }

        if (btnSalvar != null) {
            btnSalvar.setEnabled(!carregando);
        }
    }
}
