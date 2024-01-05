SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[timetable](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Time created] [datetime] NULL,
	[Time due] [datetime] NULL,
	[Event] [nvarchar](50) NULL,
	[Rating] [nvarchar](50) NULL
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[timetable] ADD  CONSTRAINT [PK_timetable] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[ArchiveCardonDelete]
ON [DB2Projekt].[dbo].[timetable]
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO [DB2Projekt].[dbo].[ArchiveTimetable] ([TimeDeleted], [Time due], [Event], [Rating])
    SELECT GETDATE(), [Time due], [Event], [Rating]
    FROM deleted;
END;
GO
ALTER TABLE [dbo].[timetable] ENABLE TRIGGER [ArchiveCardonDelete]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TRIGGER [dbo].[PreventDuplicateInput]
ON [dbo].[timetable]
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN timetable t ON i.Event = t.Event
        WHERE i.ID <> t.ID  
           AND i.Event IS NOT NULL  
    )
    BEGIN
        RAISERROR ('Event with the same name already exists. Insertion refused by Database.', 16, 1);
        ROLLBACK;
    END
END;
GO
ALTER TABLE [dbo].[timetable] ENABLE TRIGGER [PreventDuplicateInput]
GO
