package com.andersenlab.etalon.accountservice.annotation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@Slf4j
@RequiredArgsConstructor
public class WithUserIdAnalyzer {

  private final Object currentInstance;
  private final Method currentMethod;

  private Object defaultRequestBuilderValueBeforeChange;

  public void analyze() {

    log.debug(
        "{WithMockDataAnalyzer} - Analyzing {} within class {}",
        currentMethod.getName(),
        currentMethod.getDeclaringClass());

    if (!currentMethod.isAnnotationPresent(WithUserId.class)) {
      return;
    }

    log.debug("{WithMockDataAnalyzer} - Annotation present");

    Optional<MockHttpServletRequestBuilder> instantiatedRequestBuilder = getToMergeRequestBuilder();
    Optional<Field> mockMvcField = getMockMvcField(currentInstance.getClass());

    if (instantiatedRequestBuilder.isEmpty() || mockMvcField.isEmpty()) {
      return;
    }

    setMergeRequest(instantiatedRequestBuilder.get(), mockMvcField.get());
  }

  private Optional<MockHttpServletRequestBuilder> getToMergeRequestBuilder() {
    String currentUserId = currentMethod.getDeclaredAnnotation(WithUserId.class).value();
    return (StringUtils.isBlank(currentUserId))
        ? Optional.empty()
        : Optional.of(get("/").header("authenticated-user-id", currentUserId));
  }

  private Optional<Field> getMockMvcField(Class<?> clazz) {
    Optional<Field> mockMvcField =
        Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.getType().equals(MockMvc.class))
            .findFirst();

    if (mockMvcField.isEmpty()) {
      Class<?> superClass = clazz.getSuperclass();
      if (Objects.nonNull(superClass)) {
        mockMvcField = getMockMvcField(superClass);
      }
    }

    return mockMvcField;
  }

  @SneakyThrows
  private void setMergeRequest(
      MockHttpServletRequestBuilder instantiatedRequestBuilder, Field mockMvcField) {

    mockMvcField.setAccessible(true);
    Object instantiatedMockMvc = mockMvcField.get(currentInstance);
    Field defaultRequestBuilderField =
        instantiatedMockMvc.getClass().getDeclaredField("defaultRequestBuilder");

    defaultRequestBuilderField.setAccessible(true);
    this.defaultRequestBuilderValueBeforeChange =
        defaultRequestBuilderField.get(instantiatedMockMvc);
    defaultRequestBuilderField.set(instantiatedMockMvc, instantiatedRequestBuilder);
  }

  public void cleanUp() {
    Optional<Field> mockMvcField = getMockMvcField(currentInstance.getClass());

    if (mockMvcField.isEmpty()) {
      return;
    }

    setMergeRequest(
        (MockHttpServletRequestBuilder) this.defaultRequestBuilderValueBeforeChange,
        mockMvcField.get());
  }
}
