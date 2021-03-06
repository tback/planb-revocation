package org.zalando.planb.revocation.domain;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Ignore;
import org.junit.Test;
import org.zalando.planb.revocation.util.InstantTimestamp;
import org.zalando.planb.revocation.util.UnixTimestamp;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RevokedClaimsData.
 *
 * @author <a href="mailto:rodrigo.reis@zalando.de">Rodrigo Reis</a>
 */
public class RevokedClaimsDataTest extends AbstractDomainTest {

    private final static String CLAIMS[][] = {
            {"uid", "011011100"},
            {"sub", "test0"}
    };

    private final static Integer ISSUED_BEFORE = InstantTimestamp.FIVE_MINUTES_AGO.seconds();

    private final static String SERIALIZED = "{" +
            "\"claims\":{\"" + CLAIMS[0][0] + "\":\"" + CLAIMS[0][1] + "\"," +
            "\"" + CLAIMS[1][0] + "\":\"" + CLAIMS[1][1] + "\"" +
            "}," +
            "\"issued_before\":" + ISSUED_BEFORE +
            "}";

    private final static String SERIALIZED_DEFAULTS = "{" +
            "\"claims\":{\"" + CLAIMS[0][0] + "\":\"" + CLAIMS[0][1] + "\"," +
            "\"" + CLAIMS[1][0] + "\":\"" + CLAIMS[1][1] + "\"" +
            "}" +
            " }";

    private final static String SERIALIZED_INCOMPLETE = "{ " +
            "\"issued_before\":" + ISSUED_BEFORE +
            "}";

    /**
     * Tests that an exception is thrown when not setting mandatory values.
     */
    @Ignore
    @Test(expected = IllegalStateException.class)
    public void testExceptionWhenNullValues() {

        ImmutableRevokedClaimsData.builder().build();
    }

    /**
     * Tests that when instantiating a {@link RevokedClaimsData} all values are set, including the defaults.
     */
    @Test
    public void testDefaultsAreSet() {

        RevokedClaimsData claimsData = ImmutableRevokedClaimsData.builder()
                .putClaims(CLAIMS[0][0], CLAIMS[0][1])
                .putClaims(CLAIMS[1][0], CLAIMS[1][1])
                .build();

        assertThat(claimsData.issuedBefore())
                .isNotNull()
                .isLessThanOrEqualTo(UnixTimestamp.now());
    }

    /**
     * Tests JSON serialization of a {@link RevokedClaimsData} object.
     */
    @Test
    public void testJsonSerialization() throws IOException {
        RevokedClaimsData claimsData = ImmutableRevokedClaimsData.builder()
                .issuedBefore(ISSUED_BEFORE)
                .putClaims(CLAIMS[0][0], CLAIMS[0][1])
                .putClaims(CLAIMS[1][0], CLAIMS[1][1])
                .build();

        String serialized = objectMapper.writeValueAsString(claimsData);
        assertThat(serialized).isEqualTo(SERIALIZED);
    }

    /**
     * Tests JSON deserialization of a {@link RevokedClaimsData} object.
     */
    @Test
    public void testJsonDeserialization() throws IOException {

        RevokedClaimsData claimsData = objectMapper.readValue(SERIALIZED, RevokedClaimsData.class);

        assertThat(claimsData.claims().size()).isEqualTo(CLAIMS.length);
        assertThat(claimsData.claims().get(CLAIMS[0][0])).isEqualTo(CLAIMS[0][1]);
        assertThat(claimsData.claims().get(CLAIMS[1][0])).isEqualTo(CLAIMS[1][1]);
        assertThat(claimsData.issuedBefore()).isEqualTo(ISSUED_BEFORE);
    }

    /**
     * Tests that JSON deserialization of a {@link RevokedClaimsData} object fails when mandatory values are not set.
     */
    @Ignore
    @Test(expected = JsonMappingException.class)
    public void testJsonDeserializationFails() throws IOException {

        objectMapper.readValue(SERIALIZED_INCOMPLETE, RevokedClaimsData.class);
    }

    /**
     * Tests JSON deserialization of a {@link RevokedTokenData} object when the serialized string does not have values
     * that are set by default.
     */
    @Test
    public void testJsonDeserializationSetsDefaults() throws IOException {

        RevokedClaimsData claimsData = objectMapper.readValue(SERIALIZED_DEFAULTS, RevokedClaimsData.class);

        assertThat(claimsData.issuedBefore())
                .isNotNull()
                .isLessThanOrEqualTo(UnixTimestamp.now());
    }
}
