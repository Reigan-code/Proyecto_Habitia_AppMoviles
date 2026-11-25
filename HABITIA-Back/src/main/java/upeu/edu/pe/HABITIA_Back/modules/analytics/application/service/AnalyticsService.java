package upeu.edu.pe.HABITIA_Back.modules.analytics.application.service;

import upeu.edu.pe.HABITIA_Back.modules.analytics.application.dto.*;
import java.util.List;

public interface AnalyticsService {
    List<PodiumEntryDto> podium(String emailUsuario);
    WeeklySummaryDto weekly(String emailUsuario);
    BalanceDto balance(String emailUsuario);
    List<RewardDto> rewards(String emailUsuario);
}
