package uk.co.roycestonconsulting.alexa.skills.bankholidays.intent;

import static com.amazon.speech.speechlet.SpeechletResponse.newTellResponse;
import static uk.co.roycestonconsulting.alexa.skills.common.SsmlOutputSpeechBuilder.aSsmlOutputSpeechBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.SsmlOutputSpeech;
import uk.co.roycestonconsulting.alexa.skills.bankholidays.model.BankHoliday;
import uk.co.roycestonconsulting.alexa.skills.bankholidays.service.BankHolidayService;

/**
 * {@link IntentHandler} for the next bank holiday date.
 */
public class NextBankHolidayIntentHandler extends AbstractBankHolidayIntentHandler {

	private static final Logger LOG = LoggerFactory.getLogger(NextBankHolidayIntentHandler.class);

	private BankHolidayService bankHolidayService = new BankHolidayService();

	@Override
	public SpeechletResponse handleIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		LOG.debug("handleIntent requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(), requestEnvelope.getSession().getSessionId());

		BankHoliday bankHoliday = bankHolidayService.getNextBankHoliday();
		String ssmlOutput = aSsmlOutputSpeechBuilder()
				.with("The next Bank Holiday is: ", bankHoliday.getTitle())
				.withBreak(100)
				.with(" on: ")
				.withSayAs("date", "dmy", bankHoliday.getDate().format(DATE_FORMATTER))
				.build();

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
