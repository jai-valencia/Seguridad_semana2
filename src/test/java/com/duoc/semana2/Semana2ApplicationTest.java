package com.duoc.semana2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Semana2ApplicationTest {

    @Test
    void testMainMethod() {
        // Usar MockedStatic para interceptar la llamada a SpringApplication.run
        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // Configurar el mock para que no haga nada cuando se llame a run
            springApplicationMock.when(() -> SpringApplication.run(Semana2Application.class, new String[]{}))
                    .thenReturn(null);

            // Ejecutar el método main
            Semana2Application.main(new String[]{});

            // Verificar que se llamó a SpringApplication.run con los parámetros correctos
            springApplicationMock.verify(() -> 
                SpringApplication.run(eq(Semana2Application.class), any(String[].class))
            );
        }
    }

    @Test
    void testMainMethodWithArguments() {
        // Probar con argumentos
        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            String[] args = {"--server.port=8081", "--spring.profiles.active=test"};
            
            springApplicationMock.when(() -> SpringApplication.run(Semana2Application.class, args))
                    .thenReturn(null);

            Semana2Application.main(args);

            springApplicationMock.verify(() -> 
                SpringApplication.run(eq(Semana2Application.class), eq(args))
            );
        }
    }

    @Test
    void testApplicationClassExists() {
        // Verificar que la clase existe y es instanciable
        assertDoesNotThrow(() -> new Semana2Application());
    }

    @Test
    void testSpringBootApplicationAnnotation() {
        // Verificar que la clase tiene la anotación @SpringBootApplication
        assertTrue(Semana2Application.class.isAnnotationPresent(
            org.springframework.boot.autoconfigure.SpringBootApplication.class
        ));
    }

    @Test
    void testMainMethodExists() throws NoSuchMethodException {
        // Verificar que el método main existe con la firma correcta
        var mainMethod = Semana2Application.class.getDeclaredMethod("main", String[].class);
        
        assertNotNull(mainMethod);
        assertEquals("main", mainMethod.getName());
        assertEquals(void.class, mainMethod.getReturnType());
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
    }

    @Test
    void testMainMethodParameterType() throws NoSuchMethodException {
        // Verificar que main tiene el parámetro String[] correcto
        var mainMethod = Semana2Application.class.getDeclaredMethod("main", String[].class);
        var parameters = mainMethod.getParameterTypes();
        
        assertEquals(1, parameters.length);
        assertEquals(String[].class, parameters[0]);
    }

    @Test
    void testClassIsPublic() {
        // Verificar que la clase es pública
        assertTrue(java.lang.reflect.Modifier.isPublic(
            Semana2Application.class.getModifiers()
        ));
    }
}