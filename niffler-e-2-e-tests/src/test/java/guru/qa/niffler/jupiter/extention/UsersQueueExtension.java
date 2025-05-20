package guru.qa.niffler.jupiter.extention;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(String userName, String password, boolean empty) {
    }

    private static final Queue<StaticUser> EMPTY_USER = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> NOT_EMPTY_USER = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USER.add(new StaticUser("rus", "rus", true));
        NOT_EMPTY_USER.add(new StaticUser("bee", "12345", false));
        NOT_EMPTY_USER.add(new StaticUser("fish", "12345", false));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        boolean empty() default true;
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .findFirst()
                .map(p -> p.getAnnotation(UserType.class))
                .ifPresent(ut -> {
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch stopWatch = StopWatch.createStarted();
                    while (user.isEmpty() && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
                        user = ut.empty()
                                ? Optional.ofNullable(EMPTY_USER.poll())
                                : Optional.ofNullable(NOT_EMPTY_USER.poll());
                    }
                    Allure.getLifecycle().updateTestCase(testCase ->
                            testCase.setStart(new Date().getTime()));
                    user.ifPresentOrElse(
                            u -> {
                                context.getStore(NAMESPACE)
                                        .put(context.getUniqueId(), u);
                            },
                            () -> new IllegalStateException("Cant find user after 30 sec")
                    );
                });

    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        StaticUser user = context.getStore(NAMESPACE).get(context.getUniqueId(), StaticUser.class);
        if (user.empty) {
            EMPTY_USER.add(user);
        } else {
            NOT_EMPTY_USER.add(user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), StaticUser.class);
    }
}
