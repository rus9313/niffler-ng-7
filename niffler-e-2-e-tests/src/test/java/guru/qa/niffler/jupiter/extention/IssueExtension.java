package guru.qa.niffler.jupiter.extention;

import guru.qa.niffler.api.github.GhApiClient;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.SearchOption;

public class IssueExtension implements ExecutionCondition {
    private final GhApiClient ghApiClient = new GhApiClient();

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), DisabledByIssue.class)
                .or(() -> AnnotationSupport.findAnnotation(
                        context.getRequiredTestClass(),
                        DisabledByIssue.class,
                        SearchOption.INCLUDE_ENCLOSING_CLASSES))
                .map(byIssue -> "open".equals(ghApiClient.issueState(byIssue.value()))
                        ? ConditionEvaluationResult.disabled("Disable by issue " + byIssue.value())
                        : ConditionEvaluationResult.enabled("Issue enabled"))
                .orElseGet(
                        () -> ConditionEvaluationResult.enabled("Issue @DisabledByIssue not found")
                );
    }
}
