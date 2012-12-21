package games.stendhal.server.entity.mapstuff.block;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import games.stendhal.common.Direction;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.BlockTestHelper;
import utilities.RPClass.EntityTestHelper;

/**
 * Tests for the pushable block
 * 
 * @author madmetzger
 */
public class BlockTest {
	
	@BeforeClass
	public static void beforeClass() {
		BlockTestHelper.generateRPClasses();
	}

	@Test
		public final void testReset() {
			Block b = new Block(0, 0, true);
			assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
			assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
			
			b.reset();
			assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
			assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
			
			b.put("x", 2);
			b.reset();
			assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
			assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
			
			b.put("y", 2);
			b.reset();
			assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
			assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
			
			b.put("x", 2);
			b.put("y", 2);
			b.reset();
			assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
			assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		}
	
	@Test
	public void testPush() {
		Block b = new Block(0, 0, true);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(Direction.RIGHT);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(1)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(Direction.LEFT);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(Direction.UP);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(-1)));
		
		b.push(Direction.DOWN);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
	}
	
	@Test
	public void testMultiPush() {
		Block b = new Block(0, 0, false);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(Direction.RIGHT);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(1)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.push(Direction.LEFT);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(1)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(0)));
		
		b.reset();
		//after a reset the block does not count as pushed
		
		b.push(Direction.UP);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(-1)));
		
		b.push(Direction.DOWN);
		assertThat(Integer.valueOf(b.getX()), is(Integer.valueOf(0)));
		assertThat(Integer.valueOf(b.getY()), is(Integer.valueOf(-1)));
	}

}
