package urlshortener.common.repository.fixture;

import urlshortener.common.domain.Click;
import urlshortener.common.domain.ShortURL;

public class ClickFixture {

	public static Click click(ShortURL su) {
		return new Click(null, su.getHash(), null, null, null, null, null, null);
	}
}
