#ログデータを解析する際に利用
#10秒ごとにどれだけのアクセスがあったのかを出力する

require 'time'
require 'parsedate'

if( ARGV.size != 3 ) 
	puts "Filename, 基準となる時刻, countTime"
	exit
end

f = open(ARGV[0])
basicTime = Time.parse(ARGV[1])
countTime = ARGV[2].to_i
#10秒あたり何アクセスあったかを保持する
logTimeTable = Hash::new

f.each do |line|
	logs = line.split(",")
	if( logs[1] != nil ) then
		date = Time.parse( logs[3] )
		#記録時間から基準時間を引いた値
		subtime = date.to_i - basicTime.to_i
		subtime = (subtime / 10).to_i
		#一番最後の時間
		$lastsubtime = date
		#puts subtime.to_s
		if( logTimeTable[ subtime ] == nil ) then
			logTimeTable[ subtime ] = logs[2].to_i
		else
			logTimeTable[ subtime ] = logTimeTable[ subtime ] + logs[2].to_i
		end
	end
end
f.close

countTime.times do |i|
	if logTimeTable[ i ] == nil then
		puts i.to_s + " 0"
	else
		puts i.to_s + " " + logTimeTable[ i ].to_s
	end
end
