package ua.softgroup.matrix.server.supervisor.producer.token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.softgroup.matrix.server.SocketServerRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Oleksandr Tyshkovets <sg.olexander@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:test-db.properties")
@ContextConfiguration(classes = SocketServerRunner.class)
public class TokenHelperTest {

    private static final String SUBJECT = "10";
    private String token;

    @Autowired
    TokenHelper tokenHelper;

    @Before
    public void setUp() throws Exception {
        token = tokenHelper.generateToken(SUBJECT, "supervisor", new Date(new Date().getTime() + 60 * 1000));
    }

    @Test
    public void validateToken() throws Exception {
        assertTrue(tokenHelper.validateToken(token));
    }

    @Test
    public void extractSubjectFromToken() throws Exception {
        assertThat(tokenHelper.extractSubjectFromToken(token)).isEqualTo(SUBJECT);
    }

}