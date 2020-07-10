package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@SpringBootTest
class JpaTest {

    @Entity
    public static class MyEntity {
        @Id
        Long id;

        public MyEntity(final Long id) {
            this.id = id;
        }
    }


    @TestConfiguration
    static class TestConfig {
        @Primary
        @Bean
        MyRepository testBean(MyRepository real) {
            var mock = Mockito.mock(MyRepository.class, AdditionalAnswers.delegatesTo(real));

            return mock;
        }
    }

    @Autowired
    ObjectProvider<MyRepository> repository;

    @Mock
	MyRepository myRepository;

    @Test
    void verify() {
        Mockito.verify(repository.getIfAvailable(), Mockito.times(0)).count();
    }

    @Test
    void findOneById() {
        final MyEntity myEntity = new MyEntity(1L);

        Mockito.when(repository.getIfAvailable().findById(1L)).thenReturn(java.util.Optional.of(myEntity));

        assertTrue(repository.getIfAvailable().findById(1L).isPresent());
        assertEquals(myEntity, repository.getIfAvailable().findById(1L).get());
    }

    @Test
    void saveUsingSpy() {
        Mockito.when(repository.getIfAvailable().save(any(MyEntity.class))).then(returnsFirstArg());

        final MyEntity myEntity = new MyEntity(1L);
        final MyEntity persistedEntity = repository.getIfAvailable().save(myEntity);

        assertEquals(myEntity, persistedEntity);
    }

    @Test
    void saveUsingMock() {
        Mockito.when(myRepository.save(any(MyEntity.class))).then(returnsFirstArg());

        final MyEntity myEntity = new MyEntity(1L);
        final MyEntity persistedEntity = myRepository.save(myEntity);

        assertEquals(myEntity, persistedEntity);
    }

}

interface MyRepository extends JpaRepository<JpaTest.MyEntity, Long> {
}