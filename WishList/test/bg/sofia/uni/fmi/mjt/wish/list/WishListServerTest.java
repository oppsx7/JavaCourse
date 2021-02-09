package bg.sofia.uni.fmi.mjt.wish.list;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.jgroups.nio.MockSocketChannel;
import org.junit.Assert;

import java.io.IOException;

public class WishListServerTest {

    private static final int SERVER_PORT = 8080;

    @Mock
    private WishListServer server;

    private AutoCloseable closeable;
    private MockSocketChannel mockSocketChannel;
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "123";

    @Before
    public void initServer() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);
        server = new WishListServer(SERVER_PORT);
        mockSocketChannel = new MockSocketChannel();
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
        server.close();
    }

    @Test
    public void registerSuccessfullyTest() {

        String result = server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel).getMessage();
        Assert.assertEquals("[ Username testUser successfully registered ]", result);
    }

    @Test
    public void registerInvalidUsernameTest() {
        String invalidUsername = "t3stUser@";
        String result = server.registerUser(invalidUsername, TEST_PASSWORD, mockSocketChannel).getMessage();
        Assert.assertEquals(String.format("[ Username %s is invalid, select a valid one ]", invalidUsername), result);
    }

    @Test
    public void registerUsernameExistsTest() {
        MockSocketChannel mockSocketChannel1 = new MockSocketChannel();
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel1);
        String result = server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel).getMessage();
        Assert.assertEquals(String
                .format("[ Username %s is already taken, select another one ]", TEST_USERNAME), result);
    }

    @Test
    public void userSuccessfullyLoggedInTest() {
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel);
        server.logout(mockSocketChannel);
        String[] credentials = {"login", TEST_USERNAME, TEST_PASSWORD};
        String result = server.login(credentials).getMessage();
        Assert.assertEquals(String.format("[ User %s successfully logged in ]", TEST_USERNAME), result);
    }

    @Test
    public void loginMissingCredentialsTest() {
        String[] credentials = {"login"};
        String result = server.login(credentials).getMessage();
        Assert.assertEquals("[ Missing username and password ]", result);
    }

    @Test
    public void loginUserAlreadyLoggedTest() {
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel);
        String[] credentials = {"login", TEST_USERNAME, TEST_PASSWORD};
        String result = server.login(credentials).getMessage();
        Assert.assertEquals("[ You have already logged in ]", result);
    }

    @Test
    public void loginInvalidCombinationTest() {
        String[] credentials = {"login", TEST_USERNAME, TEST_PASSWORD};
        String result = server.login(credentials).getMessage();
        Assert.assertEquals("[ Invalid username/password combination ]", result);
    }

    @Test
    public void successfullyLoggedOutTest() {
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel);
        String result = server.logout(mockSocketChannel).getMessage();
        Assert.assertEquals("[ Successfully logged out ]", result);
    }

    @Test
    public void notLoggedInTest() {
        String result = server.logout(mockSocketChannel).getMessage();
        Assert.assertEquals("[ You are not logged in ]", result);
    }

    @Test
    public void postWishSuccessTest() {
        MockSocketChannel mockSocketChannel1 = new MockSocketChannel();
        String username2 = "secondName";
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel);
        server.registerUser(username2, TEST_PASSWORD, mockSocketChannel1);
        String result = server.postWish(String.format("post-wish %s ski", TEST_USERNAME)).getMessage();
        Assert.assertEquals(String
                .format("[ Gift %s for student %s submitted successfully ]", "ski", TEST_USERNAME), result);
    }

    @Test
    public void postWishUserNotRegisteredTest() {
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel);
        String result = server.postWish(String.format("post-wish %s ski", "tempName")).getMessage();
        Assert.assertEquals(String.format("[ Student with username %s is not registered ]", "tempName"), result);
    }

    @Test
    public void postWishGiftAlreadySubmittedTes() {
        MockSocketChannel mockSocketChannel1 = new MockSocketChannel();
        String username2 = "secondName";
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel);
        server.registerUser(username2, TEST_PASSWORD, mockSocketChannel1);
        server.postWish(String.format("post-wish %s ski", TEST_USERNAME));
        String result = server.postWish(String.format("post-wish %s ski", TEST_USERNAME)).getMessage();

        Assert.assertEquals(String
                .format("[The same gift for student %s was already submitted ]", TEST_USERNAME), result);
    }

    @Test
    public void emptyWishListTest() {
        String result = server.listWishes(mockSocketChannel).getMessage();
        Assert.assertEquals("[ There are no students present in the wish list ]", result);
    }

    @Test
    public void wishListTest() {
        MockSocketChannel mockSocketChannel1 = new MockSocketChannel();
        String username2 = "secondName";
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel);
        server.registerUser(username2, TEST_PASSWORD, mockSocketChannel1);
        server.postWish(String.format("post-wish %s ski", TEST_USERNAME));
        String result = server.listWishes(mockSocketChannel1).getMessage();
        Assert.assertEquals(String.format("[ %s: [ski] ]", TEST_USERNAME), result);
    }

    @Test
    public void selfWishListTest() {
        server.registerUser(TEST_USERNAME, TEST_PASSWORD, mockSocketChannel);
        server.postWish(String.format("post-wish %s ski", TEST_USERNAME));
        String result = server.listWishes(mockSocketChannel).getMessage();
        Assert.assertEquals("[ You can't view your own gifts ]", result);
    }


}
