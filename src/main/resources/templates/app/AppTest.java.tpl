package {{package}};

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void greetingMentionsTheProject() {
        assertEquals("hello from {{artifact}}", App.greeting());
    }
}