package fiji.plugin.appose;

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.gui.Roi;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.appose.util.AxisInfo;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

public class ApposeUtils
{

	/**
	 * Creates an AxisInfo object for the axes of the input image.
	 * <p>
	 * Only work for X, Y, C, Z and T axes.
	 *
	 * @param img
	 *            the input image
	 * @return the AxisInfo of the input image.
	 */
	public static AxisInfo getAxisInfo( final ImgPlus< ? > img )
	{
		final int x = img.dimensionIndex( Axes.X );
		final int y = img.dimensionIndex( Axes.Y );
		final int c = img.dimensionIndex( Axes.CHANNEL );
		final int z = img.dimensionIndex( Axes.Z );
		final int t = img.dimensionIndex( Axes.TIME );
		return new AxisInfo( x, y, c, z, t );
	}

	/**
	 * A utility to wrap an ImagePlus into an ImgPlus, without too many
	 * warnings.
	 * <p>
	 * If the input ImagePlus has a ROI, the returned ImgPlus will be a view of
	 * the original image, restricted to the bounding box of the ROI in X and Y
	 * (and with min for X and Y set to the min & max of the ROI bounding box).
	 * If the input ImagePlus does not have a ROI, the returned ImgPlus wrap the
	 * full image.
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static final < T > ImgPlus< T > rawWraps( final ImagePlus imp )
	{
		final Roi roi = imp.getRoi();
		final ImgPlus< DoubleType > img = ImagePlusAdapter.wrapImgPlus( imp );
		final ImgPlus raw = img;
		if ( roi == null )
			return raw;

		// Crop the view to the bounding box of the ROI.
		final Rectangle bounds = roi.getBounds();
		final long min[] = img.minAsLongArray();
		final long max[] = img.maxAsLongArray();
		min[ 0 ] = bounds.x;
		min[ 1 ] = bounds.y;
		max[ 0 ] = bounds.x + bounds.width - 1;
		max[ 1 ] = bounds.y + bounds.height - 1;
		final FinalInterval interval = new FinalInterval( min, max );
		final RandomAccessibleInterval view = Views.interval( raw, interval );

		final Img img2 = ImgView.wrap( view, img.factory() );
		final ImgPlus raw2 = new ImgPlus( img2, raw );
		return raw2;
	}
}
