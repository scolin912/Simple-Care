-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- 主機: 127.0.0.1
-- 產生時間： 2017-07-25 06:15:38
-- 伺服器版本: 10.1.25-MariaDB
-- PHP 版本： 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `simple_care`
--

-- --------------------------------------------------------

--
-- 資料表結構 `data_appuser`
--

CREATE TABLE `data_appuser` (
  `UserContry` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '使用者位置',
  `UserPassword` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '使用者登入密碼',
  `UserName` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '使用者名稱',
  `UserPhone` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '使用者手機號碼',
  `UserEmail` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '使用者電子郵件信箱',
  `DatetimeCreate` datetime NOT NULL COMMENT '資料建立日期時間YYYY-MM-DD hh:mm:ss',
  `DatetimeEdit` datetime NOT NULL COMMENT '資料修改日期時間YYYY-MM-DD hh:mm:ss',
  `WriterCreate` varchar(155) COLLATE utf8_unicode_ci NOT NULL COMMENT '資料建立用戶',
  `WriterEdit` varchar(155) COLLATE utf8_unicode_ci NOT NULL COMMENT '資料修改用戶'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='使用者帳號主檔資料表';

--
-- 資料表的匯出資料 `data_appuser`
--

INSERT INTO `data_appuser` (`UserContry`, `UserPassword`, `UserName`, `UserPhone`, `UserEmail`, `DatetimeCreate`, `DatetimeEdit`, `WriterCreate`, `WriterEdit`) VALUES
('Taipei', '123', 'sco', '0958888888', 'sco@maker.taiwan', '2017-07-25 03:55:47', '2017-07-25 03:55:47', 'API', 'API');

-- --------------------------------------------------------

--
-- 資料表結構 `data_central`
--

CREATE TABLE `data_central` (
  `CentralID` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '主機識別代碼',
  `CentralName` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '主機標記名稱',
  `GPS_N` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'GPS(N)',
  `DatetimeCreate` datetime NOT NULL COMMENT '資料建立日期時間YYYY-MM-DD hh:mm:ss',
  `DatetimeEdit` datetime NOT NULL COMMENT '資料修改日期時間YYYY-MM-DD hh:mm:ss',
  `GPS_E` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT 'GPS(E)',
  `UserEmail` varchar(255) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='主機裝置主檔資料表';

-- --------------------------------------------------------

--
-- 資料表結構 `log_peripheralwithcentral`
--

CREATE TABLE `log_peripheralwithcentral` (
  `PeripheralID` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '從機識別代碼',
  `CentralID` varchar(255) COLLATE utf8_unicode_ci NOT NULL COMMENT '主機識別代碼',
  `DatetimeCreate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '資料建立日期時間',
  `WriterCreate` varchar(155) COLLATE utf8_unicode_ci NOT NULL COMMENT '資料建立用戶',
  `RSSI` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主機收到從機RSSI值',
  `ID` int(11) NOT NULL COMMENT '資料表主鍵',
  `Distance` varchar(255) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='從機與主機接觸記錄資料表';

--
-- 資料表的匯出資料 `log_peripheralwithcentral`
--

INSERT INTO `log_peripheralwithcentral` (`PeripheralID`, `CentralID`, `DatetimeCreate`, `WriterCreate`, `RSSI`, `ID`, `Distance`) VALUES
('11', '22', '2017-06-23 14:37:54', '', '33', 1, '44'),
('55', '66', '2017-06-23 14:37:54', '', '77', 2, '88');

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `data_appuser`
--
ALTER TABLE `data_appuser`
  ADD PRIMARY KEY (`UserEmail`);

--
-- 資料表索引 `data_central`
--
ALTER TABLE `data_central`
  ADD PRIMARY KEY (`UserEmail`),
  ADD UNIQUE KEY `CentralID` (`CentralID`);

--
-- 資料表索引 `log_peripheralwithcentral`
--
ALTER TABLE `log_peripheralwithcentral`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `PeripheralID` (`PeripheralID`),
  ADD KEY `CentralID` (`CentralID`);

--
-- 在匯出的資料表使用 AUTO_INCREMENT
--

--
-- 使用資料表 AUTO_INCREMENT `log_peripheralwithcentral`
--
ALTER TABLE `log_peripheralwithcentral`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '資料表主鍵', AUTO_INCREMENT=3;COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
