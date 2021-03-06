package uk.co.roycestonconsulting.alexa.skills.bankholidays.intent;

import static com.amazon.speech.speechlet.SpeechletResponse.newAskResponse;
import static com.amazon.speech.speechlet.SpeechletResponse.newTellResponse;
import static uk.co.roycestonconsulting.alexa.skills.common.ResponseFactory.whatNextReprompt;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import uk.co.roycestonconsulting.alexa.skills.bankholidays.model.BankHoliday;
import uk.co.roycestonconsulting.alexa.skills.bankholidays.service.BankHolidayService;

/**
 * {@link IntentHandler} for a looking up the date of a specific bank holiday. It also looks up Easter Sunday, even though
 * it's not a bank holiday, but there's a high chance people will ask something like "when's Easter next year?".
 */
public class SpecificBankHolidayIntentHandler extends AbstractBankHolidayIntentHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SpecificBankHolidayIntentHandler.class);

	private BankHolidayService bankHolidayService = new BankHolidayService();

	@Override
	public SpeechletResponse handleIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		LOG.debug("handleIntent requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());

		Optional<Slot> bankHolidaySlot = getSlot(requestEnvelope, BANK_HOLIDAY_SLOT_NAME);
		if (!bankHolidaySlot.isPresent()) {
			LOG.debug("Cannot find bank holiday slot");
			return unknownBankHolidayResponse();
		}
		Optional<BankHolidayName> requestedBankHolidayName = BankHolidayName.fromName(bankHolidaySlot.get().getValue());
		if (!requestedBankHolidayName.isPresent()) {
			LOG.debug("Unrecognised bank holiday");
			return unknownBankHolidayResponse();
		}
		String officialName = getOfficialName(requestedBankHolidayName.get());

		Optional<BankHoliday> result = bankHolidayService.findBankHoliday(officialName, getYear(requestEnvelope));
		if (!result.isPresent()) {
			LOG.debug("Cannot find bank holiday: {}", officialName);
			PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
			speech.setText("Unable to find a matching bank holiday, please try again");
			return newAskResponse(speech, whatNextReprompt());
		}

		String ssmlOutput = buildSpecificBankHolidayOutput(requestedBankHolidayName.get(), result.get());
		LOG.debug("ssmlOutout={}", ssmlOutput);

		SsmlOutputSpeech speech = new SsmlOutputSpeech();
		speech.setSsml(ssmlOutput);
		return newTellResponse(speech);
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}
}
