package uk.nhs.careconnect.examples.fhir;

import org.hl7.fhir.dstu3.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kevinmayfield on 13/07/2017.
 */
public class CareConnectTask {
    public static Task buildFHIRTask() {
        Task task = new Task();

        Patient patient = new Patient();

        patient.setId("#pat");


        patient.addIdentifier().setSystem("https://fhir.nhs.uk/Id/nhs-number/").setValue("9876543210");

        List<UriType> profiles = new ArrayList<UriType>();
        profiles.add(new UriType("http://hl7.org.uk/CareConnect-Patient-1.structuredefinition.xml"));
        Meta meta = new Meta();
        meta.setProfile(profiles);
        patient.setMeta(meta);

        task.getFor().setResource(patient);

            task.addIdentifier().setSystem("https://tools.ietf.org/html/rfc4122").setValue("1d67c2d6-4f45-11e7-b114-b2f933d5fe66");

        CodeableConcept classCode = new CodeableConcept();
        classCode.addCoding()
                .setCode("324861000000109")
                .setSystem("http://hl7.org/fhir/task-performer-type")
                .setDisplay("Review of patient laboratory test report");
        task.setReason(classCode);


        Period period = new Period();
        period.setEnd(new Date());
        task.setExecutionPeriod(period);

        Practitioner gp = new Practitioner();

        List<UriType> gpprofiles = new ArrayList<UriType>();
        gpprofiles.add(new UriType("http://hl7.org.uk/CareConnect-Practitioner-1.structuredefinition.xml"));

        Meta metagp = new Meta();
        metagp.setProfile(gpprofiles);
        gp.setMeta(metagp);
        gp.setId("#dr");
        gp.addName()
                .setFamily("McSurname")
                .addGiven("Jeremy")
                .addPrefix("Dr");
        gp.addIdentifier().setSystem("http://fhir.nhs.net/Id/sds-user-id").setValue("123455");

        task.getRequester().setAgent(new Reference("#dr"));
        task.addContained(gp);


        CodeableConcept performerCode = new CodeableConcept();
        performerCode.addCoding()
                .setCode("performer")
                .setSystem("http://hl7.org/fhir/task-performer-type")
                .setDisplay("Performer");
        task.addPerformerType(performerCode);

        task.setStatus(Task.TaskStatus.COMPLETED);

        // Reference to original order
        task.addBasedOn().setReference ("https://fhir.uhs.nhs.uk/OrderComms/ProcedureRequest/123OrderId");

        return task;

    }
}
