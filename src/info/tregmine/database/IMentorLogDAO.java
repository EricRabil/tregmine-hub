package info.tregmine.database;

import info.tregmine.api.TregminePlayer;

public interface IMentorLogDAO {
	enum MentoringEvent {
		STARTED, COMPLETED, CANCELLED, SKIPPED;

		public MentoringEvent fromString(String str) {
			for (MentoringEvent event : MentoringEvent.values()) {
				if (event.toString().equalsIgnoreCase(str)) {
					return event;
				}
			}

			return null;
		}
	};

	public int getMentorLogId(TregminePlayer student, TregminePlayer mentor) throws DAOException;

	public void insertMentorLog(TregminePlayer student, TregminePlayer mentor) throws DAOException;

	public void updateMentorLogChannel(int mentorLogId, String channel) throws DAOException;

	public void updateMentorLogEvent(int mentorLogId, MentoringEvent event) throws DAOException;

	public void updateMentorLogResume(int mentorLogId) throws DAOException;
}
