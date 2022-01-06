package org.hl7.gravity.refimpl.sdohexchange.fhir.extract.patienttask;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Task;
import org.hl7.gravity.refimpl.sdohexchange.codes.SDCTemporaryCode;
import org.hl7.gravity.refimpl.sdohexchange.fhir.SDOHProfiles;
import org.hl7.gravity.refimpl.sdohexchange.fhir.extract.BundleExtractor;
import org.hl7.gravity.refimpl.sdohexchange.util.FhirUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PatientTaskItemInfoBundleExtractor
    extends BundleExtractor<List<PatientTaskItemInfoBundleExtractor.PatientTaskItemInfoHolder>> {

  @Override
  public List<PatientTaskItemInfoHolder> extract(Bundle bundle) {
    Map<String, Questionnaire> questionnaires = FhirUtil.getFromBundle(bundle, Questionnaire.class)
        .stream()
        .collect(Collectors.toMap(q -> q.getUrl(), Function.identity()));

    return FhirUtil.getFromBundle(bundle, Task.class)
        .stream()
        //Get only Patient tasks and not included referral tasks.
        .filter(t -> t.getMeta()
            .hasProfile(SDOHProfiles.PATIENT_TASK) && t.hasOwner() && Patient.class.getSimpleName()
            .equals(t.getOwner()
                .getReferenceElement()
                .getResourceType()))
        .map(task -> {
          String url = task.getInput()
              .stream()
              .filter(i -> SDCTemporaryCode.QUESTIONNAIRE.getCode()
                  .equals(i.getType()
                      .getCodingFirstRep()
                      .getCode()))
              .filter(c -> c.getValue() instanceof CanonicalType)
              .map(c -> ((CanonicalType) c.getValue()).getValue())
              .findAny()
              .orElse(null);

          if (!Strings.isNullOrEmpty(url) && !questionnaires.containsKey(url)) {
            String reason = String.format("Cannot resolve input questionnaire for Task resource with id '%s'.",
                task.getIdElement()
                    .getIdPart());
            throw new PatientTaskItemInfoBundleExtractorException(reason);
          }

          return new PatientTaskItemInfoHolder(task, Strings.isNullOrEmpty(url) ? null : questionnaires.get(url));
        })
        .collect(Collectors.toList());
  }

  @Getter
  @RequiredArgsConstructor
  public static class PatientTaskItemInfoHolder {

    private final Task task;
    private final Questionnaire questionnaire;
  }

  public static class PatientTaskItemInfoBundleExtractorException extends RuntimeException {

    public PatientTaskItemInfoBundleExtractorException(String message) {
      super(message);
    }
  }
}