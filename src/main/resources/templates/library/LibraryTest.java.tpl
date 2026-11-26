package {{package}};

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LibraryTest {

    @Test
    void nameIsTheArtifact() {
        assertEquals("{{artifact}}", new Library().name());
    }
}