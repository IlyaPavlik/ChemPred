-- phpMyAdmin SQL Dump
-- version 4.0.8
-- http://www.phpmyadmin.net
--
-- Хост: 127.0.0.1:3306
-- Время создания: Мар 19 2017 г., 10:56
-- Версия сервера: 5.6.14-log
-- Версия PHP: 5.5.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База данных: `periodic_table`
--

-- --------------------------------------------------------

--
-- Структура таблицы `Element`
--

CREATE TABLE IF NOT EXISTS `Element` (
  `name` varchar(20) NOT NULL,
  `symbol` char(3) NOT NULL,
  `atomic_number` int(11) NOT NULL,
  `periodic_group` int(11) DEFAULT NULL,
  `period` int(11) NOT NULL,
  `molar_mass` double NOT NULL,
  `valence` int(11) NOT NULL,
  `electronegativity` double NOT NULL,
  PRIMARY KEY (`atomic_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `Element`
--

INSERT INTO `Element` (`name`, `symbol`, `atomic_number`, `periodic_group`, `period`, `molar_mass`, `valence`, `electronegativity`) VALUES
('Hydrogen', 'H', 1, 1, 1, 1.00794, 1, 2.2),
('Helium', 'He', 2, 18, 1, 4.002602, 0, 0),
('Lithium', 'Li', 3, 1, 2, 6.941, 1, 0.99),
('Beryllium', 'Be', 4, 2, 2, 9.012182, 2, 1.57),
('Boron', 'B', 5, 13, 2, 10.811, 3, 2.04),
('Carbon', 'C', 6, 14, 2, 12.0107, 4, 2.55),
('Nitrogen', 'N', 7, 15, 2, 14.0067, 3, 3.4),
('Oxygen', 'O', 8, 16, 2, 15.9994, 2, 3.44),
('Flourine', 'F', 9, 17, 2, 18.9984032, 1, 3.98),
('Neon', 'Ne', 10, 18, 2, 20.1797, 0, 0),
('Sodium', 'Na', 11, 1, 3, 22.98977, 1, 0.93),
('Magnesium', 'Mg', 12, 2, 3, 24.305, 2, 1.31),
('Aliminum', 'Al', 13, 13, 3, 26.981538, 3, 1.61),
('Silicon', 'Si', 14, 14, 3, 28.0855, 4, 1.9),
('Phophorus', 'P', 15, 15, 3, 30.973761, 5, 2.19),
('Sulfur', 'S', 16, 16, 3, 32.065, 6, 2.58),
('Chlorine', 'Cl', 17, 17, 3, 35.453, 1, 3.16),
('Argon', 'Ar', 18, 18, 3, 39.948, 0, 0),
('Potassium', 'K', 19, 1, 4, 39.098, 1, 0.82),
('Calcium', 'Ca', 20, 2, 4, 40.078, 2, 1),
('Scandium', 'Sc', 21, 3, 4, 44.95591, 3, 1.36),
('Titanium', 'Ti', 22, 4, 4, 47.867, 4, 1.54),
('Vanadium', 'V', 23, 5, 4, 50.9415, 5, 1.63),
('Chromium', 'Cr', 24, 6, 4, 51.9961, 6, 1.66),
('Manganese', 'Mn', 25, 7, 4, 54.938049, 7, 1.55),
('Iron', 'Fe', 26, 8, 4, 55.845, 6, 1.83),
('Cobalt', 'Co', 27, 9, 4, 58.9332, 4, 1.88),
('Nickel', 'Ni', 28, 10, 4, 58.6934, 4, 1.91),
('Copper', 'Cu', 29, 11, 4, 63.546, 3, 1.9),
('Zinc', 'Zn', 30, 12, 4, 65.409, 2, 1.65),
('Galium', 'Ga', 31, 13, 4, 69.723, 3, 1.81),
('Germanium', 'Ge', 32, 14, 4, 72.64, 4, 2.01),
('Arsenic', 'As', 33, 15, 4, 74.9216, 5, 2.18),
('Selenium', 'Se', 34, 16, 4, 78.96, 6, 2.55),
('Bromine', 'Br', 35, 17, 4, 79.904, 1, 2.96),
('Krypton', 'Kr', 36, 18, 4, 83.798, 0, 0),
('Rubidium', 'Rb', 37, 1, 5, 85.4678, 1, 0.82),
('Strontium', 'Sr', 38, 2, 5, 87.62, 2, 0.95),
('Yttrium', 'Y', 39, 3, 5, 88.90585, 3, 1.22),
('Zirconium', 'Zr', 40, 4, 5, 91.224, 4, 1.33),
('Niobium', 'Nb', 41, 5, 5, 92.90638, 5, 1.6),
('Molybdenum', 'Mo', 42, 6, 5, 95.94, 6, 2.16),
('Technetium', 'Tc', 43, 7, 5, 99, 6, 2.1),
('Ruthenium', 'Ru', 44, 8, 5, 101.07, 8, 2.2),
('Rhodium', 'Rh', 45, 9, 5, 102.9055, 6, 2.28),
('Palladium', 'Pd', 46, 10, 5, 106.42, 6, 2.2),
('Silver', 'Ag', 47, 11, 5, 107.8682, 1, 1.93),
('Cadmium', 'Cd', 48, 12, 5, 112.411, 2, 1.69),
('Indium', 'In', 49, 13, 5, 114.813, 3, 1.78),
('Tin', 'Sn', 50, 14, 5, 118.71, 4, 1.96),
('Antimony', 'Sb', 51, 15, 5, 121.76, 5, 2.05),
('Tellurium', 'Te', 52, 16, 5, 127.6, 6, 2.1),
('Iodine', 'I', 53, 17, 5, 126.90447, 7, 2.66),
('Xenon', 'Xe', 54, 18, 5, 131.293, 0, 2.6),
('Cesium', 'Cs', 55, 1, 6, 132.90545, 1, 0.79),
('Barium', 'Ba', 56, 2, 6, 137.327, 2, 0.89),
('Lanthanum', 'La', 57, NULL, 6, 138.9055, 3, 1.1),
('Cerium', 'Ce', 58, NULL, 6, 140.116, 4, 1.12),
('Praseodymium', 'Pr', 59, NULL, 6, 140.90765, 3, 1.13),
('Neodymium', 'Nd', 60, NULL, 6, 144.24, 4, 1.14),
('Promethium', 'Pm', 61, NULL, 6, 145, 3, 0),
('Samarium', 'Sm', 62, NULL, 6, 150.36, 3, 1.17),
('Europium', 'Eu', 63, NULL, 6, 151.964, 3, 0),
('Gadollnium', 'Gd', 64, NULL, 6, 157.25, 3, 1.2),
('Terbium', 'Tb', 65, NULL, 6, 158.92534, 4, 0),
('Dysprosium', 'Dy', 66, NULL, 6, 162.5, 3, 1.22),
('Holmium', 'Ho', 67, NULL, 6, 164.93032, 3, 1.23),
('Erbium', 'Er', 68, NULL, 6, 167.259, 3, 1.24),
('Thulium', 'Tm', 69, NULL, 6, 168.93421, 3, 1.25),
('Ytterbium', 'Yb', 70, NULL, 6, 173.04, 3, 0),
('Luteium', 'Lu', 71, 3, 6, 174.967, 3, 1),
('Hafnium', 'Hf', 72, 4, 6, 178.49, 4, 1.3),
('Tantalum', 'Ta', 73, 5, 6, 180.947, 5, 1.5),
('Tungsten', 'W', 74, 6, 6, 183.84, 6, 1.7),
('Rhenium', 'Re', 75, 7, 6, 186.207, 7, 1.9),
('Osmium', 'Os', 76, 8, 6, 190.23, 8, 2.2),
('Iridium', 'Ir', 77, 9, 6, 192.217, 6, 2.2),
('Platinum', 'Pt', 78, 10, 6, 195.078, 6, 2.2),
('Gold', 'Au', 79, 11, 6, 196.96655, 3, 2.4),
('Mercury', 'Hg', 80, 12, 6, 200.59, 2, 1.9),
('Thallium', 'Tl', 81, 13, 6, 204.3833, 3, 1.8),
('Lead', 'Pb', 82, 14, 6, 207.2, 4, 1.8),
('Bismuth', 'Bi', 83, 15, 6, 208.98038, 5, 1.9),
('Polonium', 'Po', 84, 16, 6, 210, 6, 2),
('Astatine', 'At', 85, 17, 6, 210, 0, 2.2),
('Radon', 'Rn', 86, 18, 6, 222, 0, 0),
('Francium', 'Fr', 87, 1, 7, 223, 0, 0.7),
('Radium', 'Ra', 88, 2, 7, 226, 2, 0.9),
('Actinium', 'Ac', 89, NULL, 7, 227, 3, 1.1),
('Thorium', 'Th', 90, NULL, 7, 232.0381, 4, 1.3),
('Protactinium', 'Pa', 91, NULL, 7, 231.03588, 5, 1.5),
('Uranium', 'U', 92, NULL, 7, 238.02891, 6, 1.7),
('Neptunium', 'Np', 93, NULL, 7, 237, 0, 0),
('Plutonium', 'Pu', 94, NULL, 7, 244, 0, 0),
('Americium', 'Am', 95, NULL, 7, 243, 0, 0),
('Curium', 'Cm', 96, NULL, 7, 247, 0, 0),
('Berkelium', 'Bk', 97, NULL, 7, 247, 0, 0),
('Californium', 'Cf', 98, NULL, 7, 251, 0, 0),
('Einsteinium', 'Es', 99, NULL, 7, 254, 0, 0),
('Fermium', 'Fm', 100, NULL, 7, 257, 0, 0),
('Mendelevium', 'Md', 101, NULL, 7, 260, 0, 0),
('Nobelium', 'No', 102, NULL, 7, 259, 0, 0),
('Lawrencium', 'Lr', 103, 3, 7, 262, 0, 0),
('Rutherfordium', 'Rf', 104, 4, 7, 261, 0, 0),
('Dubnium', 'Db', 105, 5, 7, 262, 0, 0),
('Seaborgium', 'Sg', 106, 6, 7, 266, 0, 0),
('Bohrium', 'Bh', 107, 7, 7, 262, 0, 0),
('Hassium', 'Hs', 108, 8, 7, 265, 0, 0),
('Meitnerium', 'Mt', 109, 9, 7, 266, 0, 0),
('Darmstadtium', 'Ds', 110, 10, 7, 281, 0, 0),
('Roentgenium', 'Rg', 111, 11, 7, 272, 0, 0),
('Copernicium', 'Cn', 112, 12, 7, 285, 0, 0),
('Ununtrium', 'Uut', 113, 13, 7, 284, 0, 0),
('Flerovium', 'Fl', 114, 14, 7, 289, 0, 0),
('Ununpentium', 'Uup', 115, 15, 7, 288, 0, 0),
('Livermorium', 'Lv', 116, 16, 7, 293, 0, 0),
('Ununseptium', 'Uus', 117, 17, 7, 294, 0, 0),
('Ununoctium', 'Uuo', 118, 18, 7, 294, 0, 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
