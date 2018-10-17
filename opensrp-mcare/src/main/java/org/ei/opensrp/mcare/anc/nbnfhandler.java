package org.ei.opensrp.mcare.anc;

import android.content.ContentValues;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class nbnfhandler implements FormSubmissionHandler {


    public nbnfhandler() {

    }

    @Override
    public void handle(FormSubmission submission) {
        String entityID = submission.entityId();
        List<Alert> alertlist_for_client = Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "BirthNotificationFollowUp");
        if(alertlist_for_client.size() == 0){

        }else{//BirthNotificationPregnancyStatusFollowUp previous_name
            for(int i = 0;i<alertlist_for_client.size();i++){
                Context.getInstance().alertService().changeAlertStatusToComplete(entityID, "BirthNotificationFollowUp");

            }
        }
        try {
            String motherlastlmp = submission.getFieldValue("FWPSRLMP");
            if (motherlastlmp != null) {
                List<Map<String, String>> instances = submission.getSubFormByName("child_registration").instances();
                for (int i = 0; i < instances.size(); i++) {
                    String entityIDtoProcess = instances.get(i).get("id");
                    AllCommonsRepository childrepo = Context.getInstance().allCommonsRepositoryobjects("mcarechild");
                    CommonPersonObject mcarechild = childrepo.findByCaseID(entityIDtoProcess);
                    HashMap<String, String> detailstoPut = new HashMap<String, String>();
                    detailstoPut.put("FWPSRLMP", motherlastlmp);
                    childrepo.mergeDetails(entityIDtoProcess, detailstoPut);

                }
            }
        }catch (Exception e){

        }
        if(submission.getFieldValue("FWBNFSTS").equalsIgnoreCase("0") && submission.getFieldValue("user_type").equalsIgnoreCase("FWA")){
            return;
        }
        Map<String, String> overrideValue = new HashMap<String, String>();
        if(submission.getFieldValue("FWBNFSTS").equalsIgnoreCase("0") && submission.getFieldValue("user_type").equalsIgnoreCase("FD")){
            overrideValue.clear();
            CommonPersonObject motherObject = Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByCaseID(entityID);
            AllCommonsRepository motherRepo = Context.getInstance().allCommonsRepositoryobjects("mcaremother");
            overrideValue.put("FWWOMVALID","0");
            motherRepo.mergeDetails(entityID,overrideValue);

            overrideValue.clear();
            CommonPersonObject elcoObject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(motherObject.getRelationalId());
            AllCommonsRepository elcoRepo = Context.getInstance().allCommonsRepositoryobjects("elco");
            overrideValue.put("FWPSRPREGSTS","0");
            elcoRepo.mergeDetails(motherObject.getRelationalId(),overrideValue);

        }
        if(submission.getFieldValue("FWBNFSTS").equalsIgnoreCase("4") && submission.getFieldValue("user_type").equalsIgnoreCase("FD")){
            overrideValue.clear();
            CommonPersonObject motherObject = Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByCaseID(entityID);
            AllCommonsRepository motherRepo = Context.getInstance().allCommonsRepositoryobjects("mcaremother");
            if(submission.getFieldValue("FWGESTATIONALAGE")!=null){
                String ga = submission.getFieldValue("FWGESTATIONALAGE");
                try{
                    int gestationalage = Integer.parseInt(ga);
                    if(gestationalage<24){
                        overrideValue.put("FWWOMVALID","0");
                        motherRepo.mergeDetails(entityID,overrideValue);
                    }
                }catch (Exception e){

                }
            }
            overrideValue.clear();

            CommonPersonObject elcoObject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(motherObject.getRelationalId());
            AllCommonsRepository elcoRepo = Context.getInstance().allCommonsRepositoryobjects("elco");
            overrideValue.put("FWPSRPREGSTS","0");
            elcoRepo.mergeDetails(motherObject.getRelationalId(),overrideValue);

        }
        if(submission.getFieldValue("FWBNFSTS").equalsIgnoreCase("3") && submission.getFieldValue("user_type").equalsIgnoreCase("FD")){
            overrideValue.clear();
            CommonPersonObject motherObject = Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByCaseID(entityID);
            AllCommonsRepository motherRepo = Context.getInstance().allCommonsRepositoryobjects("mcaremother");
            overrideValue.clear();
            CommonPersonObject elcoObject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(motherObject.getRelationalId());
            AllCommonsRepository elcoRepo = Context.getInstance().allCommonsRepositoryobjects("elco");
            overrideValue.put("FWPSRPREGSTS","0");
            elcoRepo.mergeDetails(motherObject.getRelationalId(),overrideValue);

        }

        if(submission.getFieldValue("user_type").equalsIgnoreCase("FWA")){
            overrideValue.clear();
            AllCommonsRepository motherRepo = Context.getInstance().allCommonsRepositoryobjects("mcaremother");
            overrideValue.put("FWWOMVALID","1");
            motherRepo.mergeDetails(entityID,overrideValue);
            overrideValue.clear();
            ContentValues contentValue = new ContentValues();
            contentValue.put("Is_PNC","0");
            motherRepo.update("mcaremother",contentValue,entityID);
        }

        if(submission.getFieldValue("FWBNFSTS").equalsIgnoreCase("8") && submission.getFieldValue("user_type").equalsIgnoreCase("FD")){
            overrideValue.clear();
            CommonPersonObject motherObject = Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByCaseID(entityID);
            AllCommonsRepository motherRepo = Context.getInstance().allCommonsRepositoryobjects("mcaremother");
            overrideValue.put("FWWOMVALID","0");
            motherRepo.mergeDetails(entityID,overrideValue);

            overrideValue.clear();
            CommonPersonObject elcoObject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(motherObject.getRelationalId());
            AllCommonsRepository elcoRepo = Context.getInstance().allCommonsRepositoryobjects("elco");
            overrideValue.put("FWPSRPREGSTS","0");
            overrideValue.put("FWELIGIBLE","0");
            elcoRepo.mergeDetails(motherObject.getRelationalId(),overrideValue);
            elcoRepo.updateSearch(motherObject.getRelationalId());

        }
        if(submission.getFieldValue("FWBNFSTS").equalsIgnoreCase("999") && submission.getFieldValue("user_type").equalsIgnoreCase("FD")){
            overrideValue.clear();
            CommonPersonObject motherObject = Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByCaseID(entityID);
            AllCommonsRepository motherRepo = Context.getInstance().allCommonsRepositoryobjects("mcaremother");
            overrideValue.put("FWWOMVALID","0");
            motherRepo.mergeDetails(entityID,overrideValue);

            overrideValue.clear();
            CommonPersonObject elcoObject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(motherObject.getRelationalId());
            AllCommonsRepository elcoRepo = Context.getInstance().allCommonsRepositoryobjects("elco");
            overrideValue.put("FWPSRPREGSTS","0");
            overrideValue.put("FWELIGIBLE","0");
            elcoRepo.mergeDetails(motherObject.getRelationalId(),overrideValue);
            elcoRepo.updateSearch(motherObject.getRelationalId());

        }


        if(submission.getFieldValue("FWBNFWOMVITSTS").equalsIgnoreCase("0") && submission.getFieldValue("user_type").equalsIgnoreCase("FD")){
            overrideValue.clear();
            CommonPersonObject motherObject = Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByCaseID(entityID);
            AllCommonsRepository motherRepo = Context.getInstance().allCommonsRepositoryobjects("mcaremother");
            overrideValue.put("FWWOMVALID","0");
            motherRepo.mergeDetails(entityID,overrideValue);

            overrideValue.clear();
            CommonPersonObject elcoObject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(motherObject.getRelationalId());
            AllCommonsRepository elcoRepo = Context.getInstance().allCommonsRepositoryobjects("elco");
            overrideValue.put("FWPSRPREGSTS","0");
            overrideValue.put("FWELIGIBLE","0");
            elcoRepo.mergeDetails(motherObject.getRelationalId(),overrideValue);
            elcoRepo.updateSearch(motherObject.getRelationalId());

        }

    }
}
