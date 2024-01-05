SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetCardsFromArchive]
    @StartDate DATETIME
AS
BEGIN
    SELECT ID, Event, Rating, TimeDeleted
    FROM ArchiveTimetable
    WHERE [TimeDeleted] >= @StartDate;
END;
GO
