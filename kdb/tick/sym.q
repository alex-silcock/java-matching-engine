orders:([]time:`timespan$(); sym:`g#`symbol$(); side:`symbol$(); price:`float$(); qty:`float$(); tradeId:`long$(); stpfId:`symbol$(); stpfInstr:`symbol$());
trades:([]time:`timespan$(); sym:`g#`symbol$(); price:`float$(); qty:`float$(); idsTraded:(::)!; stpfIds:(::)!);
marketEvents:([]time:`timespan$(); sym:`g#`symbol$(); event:`char$());