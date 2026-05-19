package com.network.plantfyth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.network.plantfyth.databinding.ActivityEditPlantBinding;

public class EditPlantActivity extends AppCompatActivity {

    private ActivityEditPlantBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPlantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // EXEMPLO DE USO DA TELA (apenas visual)
        binding.editNomePlanta.setText("Minha Samambaia");
        binding.editNomeEspecie.setText("Samambaia Americana");
        binding.editNomeFamilia.setText("Polypodiaceae");

        binding.editDataPlantio.setText("12/03/2024");
        binding.editModoPlantio.setText("Muda");

        // Dados automáticos (não editáveis)
        binding.txtIrrigacaoInfo.setText("A cada 3 dias");
        binding.txtAdubacaoInfo.setText("A cada 30 dias");
        binding.txtUmidadeInfo.setText("Ideal: 70%");
        binding.txtTamanhoPrevistoInfo.setText("Atual: 12 cm • Previsto: 80 cm");
        binding.txtTamanhoVasoInfo.setText("Vaso mínimo: 25 cm");
        binding.txtObservacoesInfo.setText("Prefere locais sombreados e solo úmido.");
    }
}