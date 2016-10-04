package org.ei.opensrp.sync;

import org.json.JSONObject;
import org.mockito.Mockito;


/**
 * Created by keyman on 30/09/16.
 */
public class ProcessClientClassTest extends BaseClientProcessorTest {



    public void testProcessClientClassWhenClassIsNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(null, event, client);
            assertFalse("Client class should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processClientClass(createEmptyJsonObject(), event, client);
            assertFalse("Client class should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testProcessClientClassWhenEventIsNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject classification = createClassification();

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, null, client);
            assertFalse("Client class should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, createEmptyJsonObject(), client);
            assertFalse("Client class should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testProcessClientClassWhenClientIsNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject classification = createClassification();
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, event, null);
            assertFalse("Client class should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, event, createEmptyJsonObject());
            assertFalse("Client class should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testProcessClientClassWhenClassHasNoRules() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject classification = createClassification();
            classification.remove("rule");

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, event, client);
            assertNull("Client class should not be processed, Exception thrown", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testProcessClientClassWhenRuleHasNoFields() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject classification = createClassification();
            classification.remove("rule");
            updateJsonObject(classification, "rule", createEmptyJsonObject());
            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, event, client);
            assertNull("Client class should not be processed, Exception thrown", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testProcessClient() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject classification = createClassification().getJSONArray("case_classification_rules").getJSONObject(0);
            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);
            
            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            
            ClientProcessor spy = Mockito.spy(clientProcessor);
            Mockito.doNothing().when(spy).processField(Mockito.any(JSONObject.class), Mockito.any(JSONObject.class), Mockito.any(JSONObject.class));
            
            Boolean processed = spy.processClientClass(classification, event, client);
            assertTrue("Client class should have been processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
