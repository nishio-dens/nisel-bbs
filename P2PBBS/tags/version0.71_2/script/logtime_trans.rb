#ログデータを解析する際に利用
#ログに記録されている時間を経過時間に変換するスクリプト

require 'time'
require 'parsedate'

if( ARGV.size != 2 ) 
	puts "Filename, 基準となる時刻"
	exit
end

f = open(ARGV[0])
basicTime = Time.parse(ARGV[1])
countTime = ARGV[2].to_i

outputFile = File.open(ARGV[0] + "_trans.log", 'w')

f.each do |line|
	logs = line.split(",")
	if( logs[1] != nil ) then
		date = Time.parse( logs[3] )
		#記録時間から基準時間を引いた値
		subtime = date.to_i - basicTime.to_i
		
		#データ出力
		outputFile.puts logs[0].to_s + "," + logs[1].to_s + "," + logs[2].to_s + "," + subtime.to_s
	else
		outputFile.puts line + "\n\n\n"
	end
end
outputFile.close
f.close

