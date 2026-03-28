package de.timkodiert.mokka.monthly_overview;

import java.time.YearMonth;

public interface MonthlyOverviewService {

    MonthlyOverviewDTO generateOverview(YearMonth yearMonth);
}
