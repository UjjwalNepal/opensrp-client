package org.ei.drishti.view.customControls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.R;
import org.ei.drishti.view.contract.SmartRegisterClient;
import org.ei.drishti.view.viewHolder.ViewStubInflater;

import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.util.DateUtil.formatDate;
import static org.ei.drishti.view.controller.ECSmartRegisterController.*;

public class ClientStatusView extends FrameLayout {

    private Map<String, ViewStubInflater> statusLayouts;

    @SuppressWarnings("UnusedDeclaration")
    public ClientStatusView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public ClientStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClientStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        this.statusLayouts = new HashMap<String, ViewStubInflater>();
        ViewStubInflater commonECAndFPLayout = new ViewStubInflater((ViewStub) findViewById(R.id.ec_and_fp_status_layout));

        this.statusLayouts
                .put(EC_STATUS, commonECAndFPLayout);
        this.statusLayouts
                .put(FP_STATUS, commonECAndFPLayout);
        this.statusLayouts
                .put(ANC_STATUS, new ViewStubInflater((ViewStub) findViewById(R.id.anc_status_layout)));
        this.statusLayouts
                .put(PNC_STATUS, new ViewStubInflater((ViewStub) findViewById(R.id.pnc_status_layout)));
        this.statusLayouts
                .put(PNC_FP_STATUS, new ViewStubInflater((ViewStub) findViewById(R.id.pnc_and_fp_status_layout)));
    }

    public void bindData(SmartRegisterClient client) {
        String statusType = client.status().get(STATUS_TYPE_FIELD);
        String statusDate = formatDate(client.status().get(STATUS_DATE_FIELD));

        hideAllLayout();

        ViewGroup statusViewGroup = statusLayout(statusType);
        statusViewGroup.setVisibility(View.VISIBLE);
        dateView(statusViewGroup).setText(statusDate);

        if (EC_STATUS.equalsIgnoreCase(statusType) || FP_STATUS.equalsIgnoreCase(statusType)) {
            typeView(statusViewGroup)
                    .setText(StringUtils.upperCase(statusType));
        } else if (ANC_STATUS.equalsIgnoreCase(statusType)) {
            eddDateView(statusViewGroup)
                    .setText(formatDate(client.status().get(STATUS_EDD_FIELD)));
        } else if (PNC_FP_STATUS.equalsIgnoreCase(statusType)) {
            fpDateView(statusViewGroup)
                    .setText(formatDate(client.status().get(FP_METHOD_DATE_FIELD)));
        }
    }

    public ViewGroup statusLayout(String statusType) {
        return statusLayouts.get(statusType).get();
    }

    public void hideAllLayout() {
        for (String statusLayout : statusLayouts.keySet()) {
            statusLayouts.get(statusLayout).setVisibility(View.GONE);
        }
    }

    public TextView dateView(ViewGroup statusViewGroup) {
        return ((TextView) statusViewGroup.findViewById(R.id.txt_status_date));
    }

    public TextView typeView(ViewGroup statusViewGroup) {
        return ((TextView) statusViewGroup.findViewById(R.id.txt_status_type));
    }

    public TextView eddDateView(ViewGroup statusViewGroup) {
        return ((TextView) statusViewGroup.findViewById(R.id.txt_anc_status_edd_date));
    }

    public TextView fpDateView(ViewGroup statusViewGroup) {
        return ((TextView) statusViewGroup.findViewById(R.id.txt_fp_status_date));
    }
}
