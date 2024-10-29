import com.lowwdel.multiThreading.gameOfLife.utils.ArrayGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArrayGeneratorTest {
    @Test
    public void testGenerateArray() {
        // 创建ArrayGenerator实例
        ArrayGenerator generator = new ArrayGenerator();

        // 调用generateArray方法生成二维字节数组
        byte[][] generatedArray = generator.generateArray();

        // 验证生成的数组是否为1000x1000
        assertEquals(1000, generatedArray.length);
        for (byte[] row : generatedArray) {
            assertEquals(1000, row.length);
        }

        // 验证数组元素的值是否在0到1之间
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                assertTrue(generatedArray[i][j] == 0 || generatedArray[i][j] == 1);
            }
        }
    }
}
