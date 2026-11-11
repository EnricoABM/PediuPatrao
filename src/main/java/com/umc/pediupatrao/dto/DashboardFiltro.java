package com.umc.pediupatrao.dto;

import java.time.LocalDate;

public record DashboardFiltro(LocalDate inicio, LocalDate fim, String produtoId) {
}
