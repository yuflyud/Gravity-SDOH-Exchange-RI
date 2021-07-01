package org.hl7.gravity.refimpl.sdohexchange.fhir.factory;

import lombok.Setter;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ConditionClinicalStatusCodes;
import org.hl7.gravity.refimpl.sdohexchange.fhir.ConditionVerificationStatusCodes;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.UsCoreConditionCategory;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;
import org.springframework.util.Assert;

@Setter
public class HealthConcernBundleFactory {

  private static final String BASED_ON = "Conversation with Patient";

  private String name;
  private Coding category;
  private Coding icdCode;
  private Coding snomedCode;
  private Patient patient;
  private Practitioner practitioner;

  public Bundle createBundle() {
    Assert.notNull(name, "Name cannot be null.");
    Assert.notNull(category, "SDOH DomainCode cannot be null.");
    Assert.notNull(icdCode, "ICD-10 code cannot be null.");
    Assert.notNull(snomedCode, "SNOMED-CT code cannot be null.");
    Assert.notNull(patient, "Patient cannot be null.");
    Assert.notNull(practitioner, "Practitioner cannot be null.");

    Bundle bundle = new Bundle();
    bundle.setType(Bundle.BundleType.TRANSACTION);

    Condition healthConcern = createHealthConcern();
    bundle.addEntry(FhirUtil.createPostEntry(healthConcern));

    return bundle;
  }

  private Condition createHealthConcern() {
    Condition healthConcern = new Condition();
    healthConcern.getMeta()
        .addProfile(SDOHProfiles.CONDITION);
    ConditionClinicalStatusCodes clinicalStatus = ConditionClinicalStatusCodes.ACTIVE;
    healthConcern.setClinicalStatus(new CodeableConcept().addCoding(new Coding().setCode(clinicalStatus.toCode())
        .setSystem(clinicalStatus.getSystem())
        .setDisplay(clinicalStatus.getDisplay())));
    ConditionVerificationStatusCodes verificationStatus = ConditionVerificationStatusCodes.UNCONFIRMED;
    healthConcern.setVerificationStatus(new CodeableConcept().addCoding(new Coding().setCode(verificationStatus.toCode())
        .setSystem(verificationStatus.getSystem())
        .setDisplay(verificationStatus.getDisplay())));
    UsCoreConditionCategory healthConcernCategory = UsCoreConditionCategory.HEALTHCONCERN;
    healthConcern.addCategory()
        .addCoding(category);
    healthConcern.addCategory()
        .addCoding(new Coding().setSystem(healthConcernCategory.getSystem())
            .setCode(healthConcernCategory.toCode())
            .setDisplay(healthConcernCategory.getDisplay()));
    healthConcern.getCode()
        .setText(name);
    healthConcern.getCode()
        .addCoding(icdCode);
    healthConcern.getCode()
        .addCoding(snomedCode);
    healthConcern.setSubject(getPatientReference());
    healthConcern.setAsserter(getPatientReference());
    healthConcern.setRecorder(FhirUtil.toReference(Patient.class, practitioner.getIdElement()
        .getIdPart(), practitioner.getNameFirstRep()
        .getNameAsSingleString()));
    healthConcern.getEvidenceFirstRep()
        .getCodeFirstRep()
        .setText(BASED_ON);
    return healthConcern;
  }

  private Reference getPatientReference() {
    return FhirUtil.toReference(Patient.class, patient.getIdElement()
        .getIdPart(), patient.getNameFirstRep()
        .getNameAsSingleString());
  }
}