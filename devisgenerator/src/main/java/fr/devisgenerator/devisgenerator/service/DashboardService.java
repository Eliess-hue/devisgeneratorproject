package fr.devisgenerator.devisgenerator.service;

import fr.devisgenerator.devisgenerator.dto.response.DashboardResponse;
import fr.devisgenerator.devisgenerator.entity.AppUser;

public interface DashboardService {


    DashboardResponse getDashboard(
            AppUser user
    );
}
