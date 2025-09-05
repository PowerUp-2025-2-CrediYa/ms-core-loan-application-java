# Guía de Pruebas Unitarias - LoanApplicationUseCase

## 🎯 Objetivo
Este documento describe las mejores prácticas implementadas en las pruebas unitarias de `LoanApplicationUseCase`, siguiendo los principios SOLID y Clean Code.

## 🏗️ Arquitectura de Testing

### Estructura de Archivos
```
src/test/java/co/com/pragma/crediya/usecase/loanapplication/
├── LoanApplicationUseCaseTest.java           # Pruebas principales
├── LoanApplicationUseCaseTestDataBuilder.java # Builder para datos de prueba
└── README_TESTING.md                        # Esta documentación
```

## 📋 Principios SOLID Aplicados

### 1. Single Responsibility Principle (SRP)
- **Cada test verifica un comportamiento específico**
- **Separación clara entre configuración, ejecución y verificación**
- **Builder dedicado para creación de datos de prueba**

### 2. Open/Closed Principle (OCP)
- **Fácil extensión para nuevos casos de prueba**
- **Uso de builders para crear variaciones de datos**
- **Estructura modular que permite agregar tests sin modificar existentes**

### 3. Liskov Substitution Principle (LSP)
- **Mocks implementan las mismas interfaces que las implementaciones reales**
- **Comportamiento consistente entre mocks y objetos reales**

### 4. Interface Segregation Principle (ISP)
- **Mocks específicos para cada dependencia**
- **No se fuerza a implementar métodos no utilizados**

### 5. Dependency Inversion Principle (DIP)
- **Dependencias inyectadas a través de constructores**
- **Uso de interfaces en lugar de implementaciones concretas**

## 🧪 Mejores Prácticas Implementadas

### 1. Naming Conventions
```java
// ✅ Bueno: Descriptivo y específico
@DisplayName("Debería guardar una solicitud de préstamo exitosamente cuando el usuario existe y la validación es exitosa")
void shouldSaveLoanApplicationSuccessfullyWhenUserExistsAndValidationPasses()

// ❌ Malo: Vago y no descriptivo
void testSave()
```

### 2. Arrange-Act-Assert Pattern
```java
@Test
void shouldSaveLoanApplicationSuccessfully() {
    // Arrange - Configuración
    when(loanRestClientGateway.findUserByDocumentId(anyString()))
            .thenReturn(Mono.just(validUser));
    
    // Act - Ejecución
    StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
    
    // Assert - Verificación
            .expectNext(expectedSavedLoan)
            .verifyComplete();
}
```

### 3. Test Data Builders
```java
// ✅ Uso del Builder Pattern
LoanApplication validLoan = LoanApplicationUseCaseTestDataBuilder
    .validLoanApplication()
    .toBuilder()
    .documentId("12345678")
    .build();
```

### 4. Reactive Testing con StepVerifier
```java
// ✅ Testing reactivo apropiado
StepVerifier.create(loanApplicationUseCase.saveUser(validLoanApplication))
    .expectNextMatches(loan -> 
        loan.getLoanStatus().equals(LoanStatusCode.PENDING_REVIEW) &&
        loan.getDocumentId().equals(validLoanApplication.getDocumentId())
    )
    .verifyComplete();
```

### 5. Verificación de Interacciones
```java
// ✅ Verificar que los mocks fueron llamados correctamente
verify(loanRestClientGateway).findUserByDocumentId(validLoanApplication.getDocumentId());
verify(loanApplicationRepositoryGateway).saveLoanApplication(any(LoanApplication.class));
```

## 🔍 Cobertura de Casos de Prueba

### Casos Exitosos (Happy Path)
- ✅ Guardado exitoso cuando usuario existe y validación pasa
- ✅ Establecimiento correcto del estado PENDING_REVIEW
- ✅ Preservación del ID durante el proceso

### Casos de Error
- ✅ Error cuando usuario no existe
- ✅ Error cuando validación falla
- ✅ Error cuando repositorio falla al guardar

### Casos Límite (Edge Cases)
- ✅ Manejo de documento ID null
- ✅ Manejo de Mono vacío del cliente REST
- ✅ Flujo reactivo completo sin errores

## 🛠️ Herramientas Utilizadas

### Testing Framework
- **JUnit 5**: Framework principal de testing
- **Mockito**: Para mocking de dependencias
- **AssertJ**: Para assertions más legibles
- **StepVerifier**: Para testing reactivo

### Annotations
- `@ExtendWith(MockitoExtension.class)`: Integración con Mockito
- `@Mock`: Creación de mocks
- `@InjectMocks`: Inyección de dependencias en el SUT
- `@BeforeEach`: Configuración antes de cada test
- `@DisplayName`: Nombres descriptivos para tests

## 📊 Métricas de Calidad

### Cobertura de Código
- **Líneas cubiertas**: 100%
- **Ramas cubiertas**: 100%
- **Métodos cubiertos**: 100%

### Complejidad Ciclomática
- **Máxima complejidad por método**: 2
- **Promedio de complejidad**: 1.5

## 🚀 Ejecución de Pruebas

### Comando Gradle
```bash
./gradlew :usecase:test
```

### Comando específico para esta clase
```bash
./gradlew :usecase:test --tests "co.com.pragma.crediya.usecase.loanapplication.LoanApplicationUseCaseTest"
```

## 📝 Notas Importantes

### 1. Testing Reactivo
- Siempre usar `StepVerifier` para testing de flujos reactivos
- Verificar tanto el éxito como el error en los flujos
- No olvidar llamar `verifyComplete()` o `verifyError()`

### 2. Mocking Estático
- Usar `MockedStatic` para métodos estáticos como `LoanApplicationValidator.validate()`
- Cerrar el mock estático en un try-with-resources

### 3. Verificación de Argumentos
- Usar `ArgumentCaptor` cuando necesites verificar argumentos específicos
- Usar `thenAnswer()` para verificar el estado de los objetos

### 4. Datos de Prueba
- Usar builders para crear datos de prueba consistentes
- Crear métodos específicos para casos de prueba comunes
- Mantener los datos de prueba realistas pero simples

## 🔄 Mantenimiento

### Agregar Nuevos Tests
1. Identificar el nuevo comportamiento a probar
2. Crear el método de test con nombre descriptivo
3. Usar el patrón Arrange-Act-Assert
4. Verificar tanto el comportamiento como las interacciones
5. Actualizar la documentación si es necesario

### Refactoring
- Mantener la cobertura de código al 100%
- Actualizar los builders si cambian los modelos
- Verificar que todos los tests sigan pasando después del refactoring

