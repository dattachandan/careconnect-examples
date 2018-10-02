package uk.nhs.careconnect.unscheduled.ccriunscheduled;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@SpringBootApplication
public class CcriUnscheduledApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CcriUnscheduledApplication.class);

    private static String yasIdentifier = "https://fhir.yas.nhs.uk/Encounter/Identifier";

    private static String yasLocationIdentifier = "https://fhir.yas.nhs.uk/Location/Identifier";

    final String uuidtag = "urn:uuid:";

    FhirContext ctxFHIR = FhirContext.forDstu3();

    public static void main(String[] args) {
        SpringApplication.run(CcriUnscheduledApplication.class, args);
    }

    IGenericClient client = null;

    public static final String SNOMEDCT = "http://snomed.info/sct";


    DateFormat df = new SimpleDateFormat("HHmm_dd_MM_yyyy");

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && args[0].equals("exitcode")) {
            throw new Exception();
        }

        //client = ctxFHIR.newRestfulGenericClient("https://data.developer.nhs.uk/ccri-fhir/STU3/");
        client = ctxFHIR.newRestfulGenericClient("http://127.0.0.1:8183/ccri-fhir/STU3/");
        client.setEncoding(EncodingEnum.XML);

        Integer idno = 1;
        Integer locno = 1;

        FhirBundleUtil fhirBundle = new FhirBundleUtil(Bundle.BundleType.COLLECTION);

        Bundle patientBundle = getPatientBundle("1");
        fhirBundle.processBundleResources(patientBundle);

        Bundle bundle = new Bundle();

        Organization yas = getOrganization("RX8");
        yas.setId(fhirBundle.getNewId(yas));

        Organization lth = getOrganization("RR8");
        lth.setId(fhirBundle.getNewId(lth));


        bundle.addEntry().setResource(yas);
        bundle.addEntry().setResource(lth);

        Location patientLoc = new Location();
        patientLoc.setId(fhirBundle.getNewId(patientLoc));
        patientLoc.setStatus(Location.LocationStatus.ACTIVE);
        patientLoc.setName("Casuaulty Location");

        patientLoc.getType().addCoding()
                .setSystem("http://hl7.org/fhir/v3/RoleCode")
                .setCode("ACC")
                .setDisplay("Accident Site");
        patientLoc.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue("0113 12341234")
                .setUse(ContactPoint.ContactPointUse.MOBILE);
        patientLoc.addIdentifier().setSystem(yasIdentifier).setValue(idno.toString());
        patientLoc.getPhysicalType().addCoding()
                .setSystem( "http://hl7.org/fhir/location-physical-type")
                .setCode("bu")
                .setDisplay("Building");
        patientLoc.getPosition()
                .setAltitude(0)
                .setLatitude(53.796284092469236)
                .setLongitude(-1.5508230590282892);
        locno++;
        bundle.addEntry().setResource(patientLoc);


        Location jimmy = new Location();
        jimmy.setId(fhirBundle.getNewId(jimmy));
        jimmy.setStatus(Location.LocationStatus.ACTIVE);
        jimmy.setName("St James's University Hospital: Emergency Department");
        jimmy.setDescription("St James's University Hospital: Emergency Department");
        jimmy.getType().addCoding()
                .setSystem("http://hl7.org/fhir/v3/RoleCode")
                .setCode("ETU")
                .setDisplay("Emergency Trauma Unit");
        jimmy.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue("airwave-27051940")
                .setUse(ContactPoint.ContactPointUse.MOBILE);
        jimmy.addIdentifier().setSystem(yasIdentifier).setValue(idno.toString());
        jimmy.getPhysicalType().addCoding()
                .setSystem( "http://hl7.org/fhir/location-physical-type")
                .setCode("bu")
                .setDisplay("Building");
        jimmy.getPosition()
                .setAltitude(0)
                .setLatitude(53.80634615690993)
                .setLongitude(-1.5230420347013478);
        jimmy.setManagingOrganization(new Reference(uuidtag+lth.getIdElement().getIdPart()));
        locno++;
        bundle.addEntry().setResource(jimmy);

        Location ambulanceVech = new Location();
        ambulanceVech.setId(fhirBundle.getNewId(ambulanceVech));
        ambulanceVech.setStatus(Location.LocationStatus.ACTIVE);
        ambulanceVech.setName("Danzig");
        ambulanceVech.setDescription("Box Body Ambulance");
        ambulanceVech.getType().addCoding()
                .setSystem("http://hl7.org/fhir/v3/RoleCode")
                .setCode("AMB")
                .setDisplay("Ambulance");
        ambulanceVech.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue("airwave-542329")
                .setUse(ContactPoint.ContactPointUse.MOBILE);
        ambulanceVech.addIdentifier().setSystem(yasIdentifier).setValue(idno.toString());
        ambulanceVech.getPhysicalType().addCoding()
                .setSystem( "http://hl7.org/fhir/location-physical-type")
                .setCode("ve")
                .setDisplay("Vehicle");
        ambulanceVech.getPosition()
                .setAltitude(0)
                .setLatitude(53.795387709017916)
                .setLongitude(-1.5295702591538431);
        ambulanceVech.setManagingOrganization(new Reference(uuidtag+yas.getIdElement().getIdPart()));
        locno++;
        bundle.addEntry().setResource(ambulanceVech);



        Encounter encounter = new Encounter();
        encounter.setId(fhirBundle.getNewId(encounter));
        encounter.setSubject(new Reference(uuidtag+fhirBundle.getPatient().getId()));
        encounter.setStatus(Encounter.EncounterStatus.INPROGRESS);
        encounter.addIdentifier().setSystem(yasIdentifier).setValue(idno.toString());
        encounter.setServiceProvider(new Reference(uuidtag+yas.getIdElement().getIdPart()));
        encounter.getClass_().setCode("EMER").setSystem("http://hl7.org/fhir/v3/ActCode").setDisplay("emergency");
        encounter.addType().addCoding()
                .setSystem("http://snomed.info/sct")
                .setCode("409971007")
                .setDisplay("Emergency medical services");
        idno++;
        bundle.addEntry().setResource(encounter);


        Encounter triage = new Encounter();
        triage.setId(fhirBundle.getNewId(triage));
        triage.setSubject(new Reference(uuidtag+fhirBundle.getPatient().getId()));
        triage.setStatus(Encounter.EncounterStatus.FINISHED);
        triage.addIdentifier().setSystem(yasIdentifier).setValue(idno.toString());
        triage.setServiceProvider(new Reference(uuidtag+yas.getIdElement().getIdPart()));
        triage.getClass_().setCode("EMER").setSystem("http://hl7.org/fhir/v3/ActCode").setDisplay("emergency");
        triage.addType().addCoding()
                .setSystem("http://snomed.info/sct")
                .setCode("245581009")
                .setDisplay("Emergency examination for triage");
        triage.addLocation().setLocation(new Reference(uuidtag+patientLoc.getId()));
        triage.setPartOf(new Reference(uuidtag+encounter.getId()));
        idno++;
        bundle.addEntry().setResource(triage);


        Encounter ambulance = new Encounter();
        ambulance.setId(fhirBundle.getNewId(ambulance));
        ambulance.setSubject(new Reference(uuidtag+fhirBundle.getPatient().getId()));
        ambulance.setStatus(Encounter.EncounterStatus.INPROGRESS);
        ambulance.addIdentifier().setSystem(yasIdentifier).setValue(idno.toString());
        ambulance.setServiceProvider(new Reference(uuidtag+yas.getIdElement().getIdPart()));
        ambulance.addType().addCoding()
                .setSystem("http://snomed.info/sct")
                .setCode("11424001")
                .setDisplay("Ambulance-based care");
        ambulance.setPartOf(new Reference(uuidtag+encounter.getId()));
        ambulance.addLocation()
                .setLocation(new Reference(uuidtag+ambulanceVech.getId()))
                .setStatus(Encounter.EncounterLocationStatus.ACTIVE);

        ambulance.addLocation()
                .setLocation(new Reference(uuidtag+patientLoc.getId()))
                .setStatus(Encounter.EncounterLocationStatus.COMPLETED);

        ambulance.addLocation()
                .setLocation(new Reference(uuidtag+jimmy.getId()))
                .setStatus(Encounter.EncounterLocationStatus.PLANNED);
        idno++;
        bundle.addEntry().setResource(ambulance);


       // System.out.println(ctxFHIR.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle));

        fhirBundle.processBundleResources(bundle);


        System.out.println(ctxFHIR.newJsonParser().setPrettyPrint(true).encodeResourceToString(fhirBundle.getFhirDocument()));

        client.create().resource(fhirBundle.getFhirDocument()).execute();

    }

    private Bundle getPatientBundle(String patientId) {
        Bundle bundle = client
                .search()
                .forResource(Patient.class)
                .where(Patient.RES_ID.exactly().code(patientId))
                .include(Patient.INCLUDE_GENERAL_PRACTITIONER)
                .include(Patient.INCLUDE_ORGANIZATION)
                .returnBundle(Bundle.class)
                .execute();
        return bundle;
    }

    private Organization getOrganization(String sdsCode) {
        Organization organization = null;
        Bundle bundle =  client
                .search()
                .forResource(Organization.class)
                .where(Organization.IDENTIFIER.exactly().code(sdsCode))

                .returnBundle(Bundle.class)
                .execute();
        if (bundle.getEntry().size()>0) {
            if (bundle.getEntry().get(0).getResource() instanceof Organization)
                organization = (Organization) bundle.getEntry().get(0).getResource();

        }
        return organization;
    }
}
