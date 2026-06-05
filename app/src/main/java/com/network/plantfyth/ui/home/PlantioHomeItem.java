package com.network.plantfyth.ui.home;

import com.network.plantfyth.model.Plantio;

/**
 * Model que o Adapter consome.
 * Agrega o Plantio + os dias calculados de cada ação.
 */
public class PlantioHomeItem {
    public Plantio plantio;
    public long diasParaIrrigar; // negativo = atrasado / 0 = hoje
    public long diasParaAdubar;
    public long diasParaPodar;
}
