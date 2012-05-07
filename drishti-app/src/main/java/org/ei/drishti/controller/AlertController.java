package org.ei.drishti.controller;

import org.ei.drishti.domain.Action;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.view.adapter.AlertAdapter;
import org.ei.drishti.domain.FetchStatus;
import org.ei.drishti.domain.Response;
import org.ei.drishti.repository.AllAlerts;
import org.ei.drishti.repository.AllSettings;
import org.ei.drishti.service.DrishtiService;

import java.util.List;

import static org.ei.drishti.domain.FetchStatus.fetchedFailed;
import static org.ei.drishti.domain.FetchStatus.nothingFetched;

public class AlertController {
    private DrishtiService drishtiService;
    private AllSettings allSettings;
    private AllAlerts allAlerts;
    private AlertAdapter adapter;
    private AllEligibleCouples allEligibleCouples;

    public AlertController(DrishtiService drishtiService, AlertAdapter adapter, AllSettings allSettings, AllAlerts allAlerts, AllEligibleCouples allEligibleCouples) {
        this.drishtiService = drishtiService;
        this.allSettings = allSettings;
        this.allAlerts = allAlerts;
        this.adapter = adapter;
        this.allEligibleCouples = allEligibleCouples;
    }

    public FetchStatus fetchNewActions() {
        String previousFetchIndex = allSettings.fetchPreviousFetchIndex();
        Response<List<Action>> response = drishtiService.fetchNewAlertActions(allSettings.fetchANMIdentifier(), previousFetchIndex);

        if (response.isFailure()) {
            return fetchedFailed;
        }

        if (response.payload().isEmpty()) {
            return nothingFetched;
        }

        for (Action action : response.payload()) {
            allAlerts.handleAction(action);
            allEligibleCouples.handleAction(action);
        }
        allSettings.savePreviousFetchIndex(response.payload().get(response.payload().size() - 1).index());
        return FetchStatus.fetched;
    }

    public void refreshAlertsFromDB() {
        adapter.updateAlerts(allAlerts.fetchAlerts());
    }

    public void changeUser() {
        allSettings.savePreviousFetchIndex("0");
        allAlerts.deleteAllAlerts();
    }
}
