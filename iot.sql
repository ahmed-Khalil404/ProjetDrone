-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mar. 15 nov. 2022 à 22:33
-- Version du serveur : 10.4.25-MariaDB
-- Version de PHP : 7.4.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `iot`
--

-- --------------------------------------------------------

--
-- Structure de la table `iot`
--

CREATE TABLE `iot` (
  `id` int(255) NOT NULL,
  `hum` varchar(255) NOT NULL,
  `temp` varchar(255) NOT NULL,
  `x` varchar(255) NOT NULL,
  `y` varchar(255) NOT NULL,
  `z` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `iot`
--

INSERT INTO `iot` (`id`, `hum`, `temp`, `x`, `y`, `z`) VALUES
(7054, '69.00', '25.00', ' -9816', ' -3600', ' 12700'),
(7055, '69.00', '25.00', ' -9816', ' -3600', ' 12700'),
(7056, '69.00', '24.00', ' -9764', ' -3544', ' 12512'),
(7057, '69.00', '24.00', ' -9764', ' -3544', ' 12512'),
(7058, '69.00', '24.00', ' -9812', ' -3624', ' 12656'),
(7059, '69.00', '24.00', ' -9812', ' -3624', ' 12656'),
(7060, '69.00', '24.00', ' -9864', ' -3420', ' 12492'),
(7061, '69.00', '24.00', ' -9864', ' -3420', ' 12492'),
(7062, '69.00', '24.00', ' -9884', ' -3500', ' 12696'),
(7063, '69.00', '24.00', ' -9884', ' -3500', ' 12696'),
(7064, '69.00', '24.00', ' -9964', ' -3468', ' 12492'),
(7065, '69.00', '24.00', ' -9964', ' -3468', ' 12492');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `iot`
--
ALTER TABLE `iot`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `iot`
--
ALTER TABLE `iot`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7066;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
