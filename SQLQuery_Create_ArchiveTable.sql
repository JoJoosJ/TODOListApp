SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ArchiveTimetable](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[TimeDeleted] [datetime] NULL,
	[Time due] [datetime] NULL,
	[Event] [nvarchar](50) NULL,
	[Rating] [nvarchar](50) NULL
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[ArchiveTimetable] ADD  CONSTRAINT [PK_ArchiveTimetable] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
