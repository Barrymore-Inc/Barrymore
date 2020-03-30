import me.uquark.barrymore.utils.LongestCommonSubstring;
import org.junit.Assert;
import org.junit.Test;

public class LCSTest {
    @Test
    public void testLCS() {
        Assert.assertEquals(LongestCommonSubstring.lcs("thisisatest", "123atest123thi123sisa"), "atest");
    }
}
